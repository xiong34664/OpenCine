package com.opencapture.opencine.appkit

import com.opencapture.opencine.media.MediaTransferRate
import kotlin.math.roundToInt

public enum class MonitorDeliveryDestination {
    NATIVE_SHARE, REMOTE_UPLOAD;
    public fun title(remoteDisplayName: String?): String = if (this == NATIVE_SHARE)
        AppkitContext.context.getString(R.string.appkit_share)
    else remoteDisplayName ?: AppkitContext.context.getString(R.string.appkit_upload)
    public fun subtitle(remoteDisplayName: String?, nativeShareSubtitle: String = "Share with other apps"): String =
        if (this == NATIVE_SHARE) nativeShareSubtitle
        else AppkitContext.context.getString(
            R.string.appkit_upload_to_project,
            remoteDisplayName ?: AppkitContext.context.getString(R.string.appkit_remote),
        )
}
public enum class MonitorDeliveryPostExportAction { SYSTEM_SHARE, SAVE_TO_PHOTOS }
public data class MonitorDeliveryExportConfiguration<C, F>(
    public val logTransform: C, public val exportFormat: F,
    public val bakeLUT: Boolean = true, public val bakeLUTExposure: Boolean = true,
    public val convertLog: Boolean = false, public val includeMetadata: Boolean = true,
    public val forceReupload: Boolean = false,
)
public data class MonitorDeliveryBatchProgress(
    public val destination: MonitorDeliveryDestination, public val totalClips: Int,
    public val clipIndex: Int, public val clipFraction: Double,
    public val isCaching: Boolean = false, public val isSwitchingNetworks: Boolean = false,
    public val filename: String? = null, public val uploadCaption: String? = null, public val stage: Stage = Stage.PREPARING,
    /** The camera download's speed while caching ("42 MB/s", "Stalled"); null hides it. */
    public val transferRate: String? = null,
) {
    public enum class Stage { PREPARING, CACHING, EXPORTING, UPLOADING, SAVING, SWITCHING_NETWORKS, AWAITING_PHOTOS_ACCESS }
    public val overallFraction: Double get() = if (totalClips > 0)
        ((maxOf(0, clipIndex - 1).toDouble() + clipFraction) / totalClips).coerceAtMost(1.0) else 0.0
    public val isPreparingClip: Boolean get() = clipFraction <= 0.0
    public val showsBusySpinner: Boolean get() = isPreparingClip || isSwitchingNetworks || stage == Stage.SAVING || stage == Stage.AWAITING_PHOTOS_ACCESS
    public val percentText: String get() = if (isPreparingClip) "" else "${(overallFraction * 100).roundToInt()}%"
    public fun statusLine(uploadCaption: String? = null): String {
        if (isSwitchingNetworks) return AppkitContext.context.getString(R.string.appkit_switching_networks)
        if (stage == Stage.AWAITING_PHOTOS_ACCESS) return AppkitContext.context.getString(R.string.appkit_waiting_for_photos_access)
        if (stage == Stage.SAVING) return AppkitContext.context.getString(R.string.appkit_saving_to_photos)
        val verb = if (isCaching) {
            if (isPreparingClip) AppkitContext.context.getString(R.string.appkit_caching_from_camera_ellipsis)
            else AppkitContext.context.getString(R.string.appkit_caching_from_camera)
        } else if (destination == MonitorDeliveryDestination.NATIVE_SHARE) {
            if (clipFraction > 0 && clipFraction < 0.95) AppkitContext.context.getString(R.string.appkit_exporting)
            else if (isPreparingClip) AppkitContext.context.getString(R.string.appkit_preparing_ellipsis)
            else AppkitContext.context.getString(R.string.appkit_preparing_to_share)
        } else {
            if (clipFraction > 0 && clipFraction < 0.45) AppkitContext.context.getString(R.string.appkit_exporting)
            else if (isPreparingClip) AppkitContext.context.getString(R.string.appkit_preparing_ellipsis)
            else uploadCaption ?: this.uploadCaption ?: AppkitContext.context.getString(R.string.appkit_uploading)
        }
        // Before the first byte the only reading is Stalled: it still shows.
        if (isCaching && isPreparingClip && transferRate != null)
            return AppkitContext.context.getString(R.string.appkit_caching_from_camera) + " · $transferRate"
        if (isPreparingClip) return verb
        if (isCaching && transferRate != null) return "$verb $percentText · $transferRate"
        return "$verb $percentText"
    }
}
public sealed interface MonitorDeliveryBatchOutcome<A> {
    public data class Share<A>(public val urls: List<A>, public val metadataText: String?, public val stagingCleanupURL: A?) : MonitorDeliveryBatchOutcome<A>
    public data class SavedToPhotos<A>(public val count: Int) : MonitorDeliveryBatchOutcome<A>
    public data class Uploaded<A>(public val summary: String) : MonitorDeliveryBatchOutcome<A>
    public data class Failed<A>(public val message: String) : MonitorDeliveryBatchOutcome<A>
}
public class MonitorDeliveryAdmission(private val current: () -> Boolean) {
    public fun isCurrent(): Boolean = current()
}
public class MonitorDeliveryOriginalService<R>(
    public val available: (R) -> Boolean, public val acquire: suspend (R) -> Unit, public val progress: (R) -> Double?,
    /** Whether the last acquisition of an uncached record ended because the camera stopped answering. */
    public val cameraStoppedResponding: (R) -> Boolean = { false },
    /** The running acquisition's download speed, read with its progress; null shows none. */
    public val rate: (R) -> MediaTransferRate? = { null },
)
public class MonitorDeliveryExportService<R, A>(
    public val release: suspend (A) -> Unit, public val prepare: suspend (R, (Double) -> Unit) -> A?,
) {
    public constructor(prepare: suspend (R, (Double) -> Unit) -> A?) : this({}, prepare)
}
public data class MonitorDeliveryShareFiles<A>(public val urls: List<A>, public val metadataText: String? = null, public val cleanup: A? = null)
/** One OS insert per item, so a rejected item never discards the others (A2-56). */
public class MonitorDeliveryPhotosService<A>(public val save: suspend (A) -> Boolean)
/** Existing OS authorization, admitted before cache or export work. */
public class MonitorDeliveryPhotosAuthorizationService(
    public val isPending: () -> Boolean, public val authorize: suspend () -> Unit,
)
/**
 * The remote's record of confirmed uploads (A2-53): a record already uploaded is skipped before
 * any cache or export work unless the operator asks to re-upload; every confirmed upload is
 * recorded.
 */
public class MonitorDeliveryUploadHistory<R>(public val wasUploaded: (R) -> Boolean, public val recordUploaded: (R) -> Unit)
public class MonitorDeliveryUploadService<A>(public val progressCaption: String = "Uploading", public val upload: suspend (A, (Double) -> Unit) -> Unit)
public class MonitorDeliveryInternetService<H>(
    /** A new route lease, or null when another hop holds the route (never stolen). */
    public val needsHop: () -> Boolean, public val begin: () -> H?, public val owns: (H) -> Boolean,
    public val wait: suspend (H) -> Boolean, public val restoreEntered: suspend (H) -> Unit,
    public val restore: (H) -> Unit,
) {
    public constructor(needsHop: () -> Boolean, begin: () -> H?, owns: (H) -> Boolean,
        wait: suspend (H) -> Boolean, restore: (H) -> Unit) :
        this(needsHop, begin, owns, wait, { restore(it) }, restore)
}
public data class MonitorDeliveryBatchCounts(
    public val prepared: Int, public val uploaded: Int, public val skipped: Int,
    public val failed: Int, public val uncached: Int,
)
/**
 * The one batch policy every app runs (A2-56): a failing item never stops the others, Photos
 * access is asked before any cache or export work, and the outcome counts every item.
 */
public object MonitorDeliveryBatchPolicy {
    public fun cacheFailure(count: Int): String =
        AppkitContext.context.getString(R.string.appkit_cache_failure, count)
    /** The link died mid-transfer: the camera stopped answering while caching. */
    public fun cameraStoppedResponding(count: Int): String =
        AppkitContext.context.getString(R.string.appkit_camera_stopped_responding, count)
    public val EMPTY_SELECTION: String
        get() = AppkitContext.context.getString(R.string.appkit_select_at_least_one_clip)
    public val INTERNET_FAILURE: String
        get() = AppkitContext.context.getString(R.string.appkit_internet_failure)
    /** The note shared beside a partial native share, or null when every item was prepared. */
    public fun nativeSummary(counts: MonitorDeliveryBatchCounts): String? = buildList {
        if (counts.failed > 0) add(AppkitContext.context.getString(R.string.appkit_prepared_failed, counts.prepared, counts.failed))
        if (counts.uncached > 0) add(AppkitContext.context.getString(R.string.appkit_clips_couldnt_cache, counts.uncached))
    }.joinToString(" ").ifEmpty { null }
    /** A Photos save that saved nothing or rejected an item fails with its exact counts. */
    public fun photosFailure(saved: Int, failed: Int): String? =
        if (saved == 0 || failed > 0) AppkitContext.context.getString(R.string.appkit_saved_failed_photos, saved, failed) else null
}

public fun <A> MonitorDeliveryBatchOutcome<A>.completion(): MonitorDeliveryCompletion<MonitorDeliveryBatchOutcome<A>, MonitorDeliveryShareFiles<A>> = when (this) {
    is MonitorDeliveryBatchOutcome.Share -> MonitorDeliveryCompletion(this, share = MonitorDeliveryShareFiles(urls, metadataText, stagingCleanupURL))
    is MonitorDeliveryBatchOutcome.SavedToPhotos -> MonitorDeliveryCompletion(this, toast = AppkitContext.context.getString(R.string.appkit_saved_to_photos, count))
    is MonitorDeliveryBatchOutcome.Uploaded -> MonitorDeliveryCompletion(this, toast = summary)
    is MonitorDeliveryBatchOutcome.Failed -> MonitorDeliveryCompletion(this, toast = message)
}
