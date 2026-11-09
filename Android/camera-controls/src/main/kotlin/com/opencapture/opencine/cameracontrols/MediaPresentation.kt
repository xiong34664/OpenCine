package com.opencapture.opencine.cameracontrols

import com.opencapture.opencine.vocabulary.MediaClipFormatting
import com.opencapture.opencine.vocabulary.MediaFile
import com.opencapture.opencine.vocabulary.MediaKind
import com.opencapture.opencine.vocabulary.MediaLibraryTab

enum class MediaBrowserLayout {
    GRID,
    LIST,
}

enum class MediaThumbnailSize {
    SMALL,
    MEDIUM,
    LARGE,
    ;

    val gridMinimumDp: Int
        get() =
            when (this) {
                SMALL -> 148
                MEDIUM -> 210
                LARGE -> 280
            }

    val gridMaximumDp: Int
        get() =
            when (this) {
                SMALL -> 200
                MEDIUM -> 300
                LARGE -> 380
            }

    val gridIconSizeDp: Int
        get() =
            when (this) {
                SMALL -> 9
                MEDIUM -> 12
                LARGE -> 15
            }

    val accessibilityLabel: String
        get() =
            when (this) {
                SMALL -> CameraControlsContext.context.getString(R.string.cc_small_thumbnails)
                MEDIUM -> CameraControlsContext.context.getString(R.string.cc_medium_thumbnails)
                LARGE -> CameraControlsContext.context.getString(R.string.cc_large_thumbnails)
            }
}

object MediaClipPresentation {
    fun resolutionLabel(resolution: String?): String? {
        if (resolution.isNullOrEmpty()) return null
        val width = resolution.substringBefore('x').toIntOrNull()
        return when {
            width == null -> resolution
            width >= 3840 -> "4K"
            width >= 2560 -> "2.7K"
            width >= 1920 -> "1080p"
            width >= 1280 -> "720p"
            else -> resolution
        }
    }

    fun dateLabel(dateKey: String): String {
        if (dateKey.length != 8) return dateKey
        return "${dateKey.substring(0, 4)}-${dateKey.substring(4, 6)}-${dateKey.substring(6, 8)}"
    }

    fun metadataLine(file: MediaFile, durationOverride: String? = null): String {
        val parts = ArrayList<String>(3)
        resolutionLabel(file.resolution)?.let { parts.add(it) }
        val bytes = MediaClipFormatting.byteLabel(file.sizeBytes)
        if (bytes.isNotEmpty()) parts.add(bytes)
        if (file.kind == MediaKind.VIDEO) {
            parts.add(durationOverride ?: MediaClipFormatting.durationLabel(file.durationSeconds))
        }
        return parts.joinToString(" · ")
    }
}

/** Operator-facing media-browser copy. Never name a sister app. */
object MediaLibraryCopy {
    val FILTER_EMPTY get() = CameraControlsContext.context.getString(R.string.cc_filter_empty)
    val EMPTY_ALL get() = CameraControlsContext.context.getString(R.string.cc_empty_all)
    val EMPTY_FAVORITES get() = CameraControlsContext.context.getString(R.string.cc_empty_favorites)
    val EMPTY_VIDEOS get() = CameraControlsContext.context.getString(R.string.cc_empty_videos)
    val EMPTY_PHOTOS get() = CameraControlsContext.context.getString(R.string.cc_empty_photos)
    val DISCONNECTED get() = CameraControlsContext.context.getString(R.string.cc_connect_to_list_clips)
    val DISCONNECTED_EMPTY_CACHE get() = CameraControlsContext.context.getString(R.string.cc_no_cached_connect_to_list)
    /**
     * Offline, the gallery shows only clips cached on this phone; the last card listing stays
     * hidden (the documented offline browse: downloaded clips only), and the gallery says so.
     */
    val DISCONNECTED_CACHED_ONLY get() = CameraControlsContext.context.getString(R.string.cc_disconnected_cached_only)
    val BROWSE_HINT get() = CameraControlsContext.context.getString(R.string.cc_browse_hint)
    val SELECTING_HINT get() = CameraControlsContext.context.getString(R.string.cc_selecting_hint)
    val READING_HINT get() = CameraControlsContext.context.getString(R.string.cc_reading_camera_media)
    val LISTING_TITLE get() = CameraControlsContext.context.getString(R.string.cc_listing_title)
    val FILTER_EMPTY_TITLE get() = CameraControlsContext.context.getString(R.string.cc_filter_empty_title)
    val DISCONNECTED_TITLE get() = CameraControlsContext.context.getString(R.string.cc_disconnected_title)
    val NO_CACHED_TITLE get() = CameraControlsContext.context.getString(R.string.cc_no_cached_title)
    val EMPTY_TITLE get() = CameraControlsContext.context.getString(R.string.cc_empty_title)

    /** The gallery's hint line under its item count (Swift `MonitorMediaGalleryCopy.galleryHint`). */
    fun galleryHint(fetching: Boolean, selecting: Boolean, isLive: Boolean): String =
        when {
            fetching -> READING_HINT
            selecting -> SELECTING_HINT
            isLive -> BROWSE_HINT
            else -> DISCONNECTED_CACHED_ONLY
        }

    /**
     * The empty gallery's heading, in the same order as [emptySubtitle]: "No clips yet" only when a
     * connected camera listed nothing for this tab.
     */
    fun emptyTitle(listing: Boolean, isLive: Boolean, hasListedClips: Boolean, activeFilterCount: Int): String =
        when {
            listing -> LISTING_TITLE
            !isLive && !hasListedClips -> DISCONNECTED_TITLE
            activeFilterCount > 0 -> FILTER_EMPTY_TITLE
            !isLive -> NO_CACHED_TITLE
            else -> EMPTY_TITLE
        }
    val PROXY_TAG get() = CameraControlsContext.context.getString(R.string.cc_proxy_tag)
    val PROXY_HELP get() = CameraControlsContext.context.getString(R.string.cc_proxy_help)

    fun headerTitle(category: MediaLibraryTab): String =
        when (category) {
            MediaLibraryTab.ALL -> CameraControlsContext.context.getString(R.string.cc_all_clips)
            MediaLibraryTab.VIDEOS -> CameraControlsContext.context.getString(R.string.cc_videos)
            MediaLibraryTab.PHOTOS -> CameraControlsContext.context.getString(R.string.cc_photos)
            MediaLibraryTab.FAVORITES -> CameraControlsContext.context.getString(R.string.cc_favorites)
        }

    /**
     * The empty gallery's line: offline with nothing listed, filtered (before the cache is blamed,
     * ZIB-413), offline with listed clips, or the tab's own.
     */
    fun emptySubtitle(isLive: Boolean, hasListedClips: Boolean, activeFilterCount: Int, category: MediaLibraryTab): String =
        when {
            !isLive && !hasListedClips -> DISCONNECTED
            activeFilterCount > 0 -> FILTER_EMPTY
            !isLive -> DISCONNECTED_EMPTY_CACHE
            category == MediaLibraryTab.FAVORITES -> EMPTY_FAVORITES
            category == MediaLibraryTab.VIDEOS -> EMPTY_VIDEOS
            category == MediaLibraryTab.PHOTOS -> EMPTY_PHOTOS
            else -> EMPTY_ALL
        }
}

object MediaOperatorCopy {
    val LISTING get() = CameraControlsContext.context.getString(R.string.cc_listing_camera_clips)
    val NOT_CONNECTED get() = CameraControlsContext.context.getString(R.string.cc_connect_to_list_clips_short)
    val PLAYBACK_FAILED get() = CameraControlsContext.context.getString(R.string.cc_playback_failed)
    val BROWSING_ON_CAMERA get() = CameraControlsContext.context.getString(R.string.cc_browsing_on_camera)
    val NO_CLIPS get() = CameraControlsContext.context.getString(R.string.cc_no_clips)
    val LIST_FAILED get() = CameraControlsContext.context.getString(R.string.cc_list_failed)
    val NOT_DELETABLE get() = CameraControlsContext.context.getString(R.string.cc_not_deletable)
    val DELETE_FAILED get() = CameraControlsContext.context.getString(R.string.cc_delete_failed)
    val DOWNLOAD_FAILED get() = CameraControlsContext.context.getString(R.string.cc_download_failed)
    val THUMB_FAILED get() = CameraControlsContext.context.getString(R.string.cc_thumb_failed)
    val CLIP_OPEN_FAILED get() = CameraControlsContext.context.getString(R.string.cc_clip_open_failed)
    val CLIP_LOADING get() = CameraControlsContext.context.getString(R.string.cc_clip_loading)
    val CLIP_NOT_CACHED get() = CameraControlsContext.context.getString(R.string.cc_clip_not_cached)
}
