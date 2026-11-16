package com.opencapture.monitorui

import com.opencapture.opencine.media.MonitorConformPreview

/**
 * What the shared playback screen says to the operator and to assistive technologies: control
 * labels, their states and hints, the clip information sheet and the deletion confirmation.
 *
 * The Compose twin of `MonitorPlaybackCopy` (`Sources/MonitorPresentation/MonitorPlaybackCopy.swift`).
 * Nothing links the two, so `Fixtures/playback-copy.tsv` lists every string and both platforms'
 * tests read it (`MonitorPlaybackCopyVectorTests`, `MonitorPlaybackCopyVectorTest`): a label
 * edited on one side only fails a test.
 *
 * One accepted platform difference: [HIDE_CONTROLS_HINT] is read only on iOS, as the hide
 * control's accessibility hint. Compose has no hint semantics; TalkBack builds its own usage hint
 * from the click action, so Android carries the string but does not show it.
 */
object MonitorPlaybackCopy {
    // Header chrome.
    val BACK_TO_MEDIA get() = MonitorContext.context.getString(R.string.mui_back_to_media)
    val FAVORITE get() = MonitorContext.context.getString(R.string.mui_favorite_clip)
    val CLIP_INFORMATION get() = MonitorContext.context.getString(R.string.mui_clip_information)
    val SHARE get() = MonitorContext.context.getString(R.string.mui_share_clip)
    val SHARE_TITLE get() = MonitorContext.context.getString(R.string.mui_share_title)
    val DELETE get() = MonitorContext.context.getString(R.string.mui_delete_clip_from_camera)

    // Clip information sheet.
    val CLIP_INFORMATION_TITLE get() = MonitorContext.context.getString(R.string.mui_clip_info_title)
    val CLOSE_CLIP_INFORMATION get() = MonitorContext.context.getString(R.string.mui_close_clip_information)

    // Deletion confirmation, when the host supplies no captured copy of its own.
    val DELETE_CONFIRMATION_TITLE get() = MonitorContext.context.getString(R.string.mui_delete_confirmation_title)
    val DELETE_CONFIRM get() = MonitorContext.context.getString(R.string.mui_delete_confirm)
    val DELETE_CANCEL get() = MonitorContext.context.getString(R.string.mui_delete_cancel)
    val DELETE_CANCEL_DESCRIPTION get() = MonitorContext.context.getString(R.string.mui_cancel_deletion)
    val DELETE_CONFIRM_DESCRIPTION get() = MonitorContext.context.getString(R.string.mui_delete_from_camera_card)

    // Transport.
    val BACK_FIFTEEN get() = MonitorContext.context.getString(R.string.mui_back_15_seconds)
    val FORWARD_FIFTEEN get() = MonitorContext.context.getString(R.string.mui_forward_15_seconds)
    val PLAY get() = MonitorContext.context.getString(R.string.mui_play)
    val PAUSE get() = MonitorContext.context.getString(R.string.mui_pause)
    val RESTART get() = MonitorContext.context.getString(R.string.mui_restart_clip)
    val PREVIOUS_CLIP get() = MonitorContext.context.getString(R.string.mui_previous_clip)
    val NEXT_CLIP get() = MonitorContext.context.getString(R.string.mui_next_clip)

    // Options.
    val CONFORM_PREVIEW get() = MonitorContext.context.getString(R.string.mui_conform_preview)
    val CLOSE_CONFORM_PREVIEW get() = MonitorContext.context.getString(R.string.mui_close_conform_preview)
    val MUTE get() = MonitorContext.context.getString(R.string.mui_mute)
    val UNMUTE get() = MonitorContext.context.getString(R.string.mui_unmute)
    val LOOP get() = MonitorContext.context.getString(R.string.mui_loop_playback)
    val ON get() = MonitorContext.context.getString(R.string.mui_on)
    val OFF get() = MonitorContext.context.getString(R.string.mui_off)
    val HIDE_CONTROLS get() = MonitorContext.context.getString(R.string.mui_hide_playback_controls)
    /** iOS reads it as the hide control's hint; Compose has no hint semantics (TalkBack derives its own). */
    val HIDE_CONTROLS_HINT get() = MonitorContext.context.getString(R.string.mui_hide_controls_hint)
    val SHOW_CONTROLS get() = MonitorContext.context.getString(R.string.mui_show_playback_controls)

    // Conform preview panel.
    val CONFORM_TITLE get() = MonitorContext.context.getString(R.string.mui_conform_title)
    /** The drum's first choice: play as recorded. */
    val REAL_TIME get() = MonitorContext.context.getString(R.string.mui_real_time)

    // Scrubber. The gesture well over the picture has no label on either platform: a screen
    // reader cannot act on it (tests find it by [MonitorPlaybackTags.PICTURE]).
    val POSITION get() = MonitorContext.context.getString(R.string.mui_playback_position)

    // Rating shade.
    val SHOW_RATING get() = MonitorContext.context.getString(R.string.mui_show_rating)
    val HIDE_RATING get() = MonitorContext.context.getString(R.string.mui_hide_rating)

    /** The one transport button: restart after the end, else pause while playing, else play. */
    fun transport(reachedEnd: Boolean, playing: Boolean): String =
        if (reachedEnd) RESTART else if (playing) PAUSE else PLAY

    fun muteToggle(muted: Boolean): String = if (muted) UNMUTE else MUTE

    /** The state read with a toggle's label (the loop chip). */
    fun toggleValue(on: Boolean): String = if (on) ON else OFF

    /** The scrubber's state: whole seconds, never negative; an unknown position reads 0. */
    fun positionValue(seconds: Double): String {
        val whole = if (seconds.isFinite()) seconds.coerceAtLeast(0.0).toLong() else 0L
        return MonitorContext.context.getString(R.string.mui_seconds, whole)
    }

    /** The conform panel's line under its title: the clip's capture rate, leading into the choices. */
    fun conformSubtitle(captureRate: Double): String = MonitorConformPreview.menuHeader(captureRate)

    fun rating(expanded: Boolean): String = if (expanded) HIDE_RATING else SHOW_RATING

    /** One star choice of the rating control. */
    fun rate(level: Int): String = MonitorContext.context.getString(R.string.mui_rate_level, level,
        MonitorContext.context.getString(if (level == 1) R.string.mui_star else R.string.mui_stars))
}

/** Test tags of shared playback nodes that carry no accessibility label. */
object MonitorPlaybackTags {
    /** The gesture well over the picture (pinch, pan, tap, frame scrub). */
    const val PICTURE = "monitor.playback.picture"
}
