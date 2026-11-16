package com.opencapture.opencine.cameracontrols.gyro

import com.opencapture.opencine.gyro.GyroCalibrationBlock
import com.opencapture.opencine.gyro.GyroCalibrationInvalidation
import com.opencapture.opencine.gyro.GyroNotice
import com.opencapture.opencine.gyro.GyroPillKind
import com.opencapture.opencine.gyro.GyroTakeStatus
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import com.opencapture.opencine.cameracontrols.CameraControlsContext
import com.opencapture.opencine.cameracontrols.R

/**
 * Every user-facing string of the shared gyro screens, the Android twin of the Swift `GyroCopy`
 * table (OpenCineCameraControls): the same member names in camelCase and the same literal texts.
 * `Tests/OpenCineCameraControlsTests/GyroCopyParityTests.swift` reads this file and checks every
 * fixed literal, so the Swift table stays the source of truth.
 *
 * No angle, sample rate or millisecond figure on a primary screen; the sample rate appears only in
 * the take page's Details section.
 */
public object GyroCopy {
    // Pill

    /** The pill's label while gyro is armed between takes. */
    public val pillArmed: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_pill_armed)

    /** The pill's label while a take is recording. */
    public val pillRecording: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_pill_recording)

    /** The pill's label while a finished take is still being written. */
    public val pillSaving: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_pill_saving)

    /** The pill's label while the open take has sensor gaps. */
    public val pillGaps: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_pill_gaps)

    /** The pill's label when logging failed; tap opens the logs. */
    public val pillError: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_pill_error)

    /** The pill's label while gyro waits for the camera. */
    public val pillOffline: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_pill_offline)

    /** The pill's label when the mount moved during the open take. */
    public val pillMountMoved: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_pill_mount_moved)

    /** The pill's visible label; empty for [GyroPillKind.HIDDEN], which renders nothing. */
    public fun pill(kind: GyroPillKind): String = when (kind) {
        GyroPillKind.HIDDEN -> ""
        GyroPillKind.OFFLINE -> pillOffline
        GyroPillKind.ARMED -> pillArmed
        GyroPillKind.RECORDING -> pillRecording
        GyroPillKind.SAVING -> pillSaving
        GyroPillKind.GAPS -> pillGaps
        GyroPillKind.MOUNT_MOVED -> pillMountMoved
        GyroPillKind.ERROR -> pillError
    }

    /** The pill's TalkBack label; the warning mark appends ". IBIS is still on". */
    public fun pillA11y(kind: GyroPillKind, warning: Boolean): String {
        val base = when (kind) {
            GyroPillKind.HIDDEN -> return ""
            GyroPillKind.ARMED -> CameraControlsContext.context.getString(R.string.cc_gyro_armed)
            GyroPillKind.RECORDING -> CameraControlsContext.context.getString(R.string.cc_gyro_recording)
            GyroPillKind.SAVING -> CameraControlsContext.context.getString(R.string.cc_gyro_saving_take)
            GyroPillKind.GAPS -> CameraControlsContext.context.getString(R.string.cc_gyro_recording_gaps)
            GyroPillKind.ERROR -> CameraControlsContext.context.getString(R.string.cc_gyro_error_opens_logs)
            GyroPillKind.OFFLINE -> CameraControlsContext.context.getString(R.string.cc_gyro_waiting_camera)
            GyroPillKind.MOUNT_MOVED -> CameraControlsContext.context.getString(R.string.cc_gyro_phone_mount_moved)
        }
        return if (warning) base + CameraControlsContext.context.getString(R.string.cc_gyro_ibis_still_on) else base
    }

    // Arming sheet

    /** The arming sheet's title. */
    public val ibisTitle: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_ibis_title)

    /** Why the camera's own stabilization has to be off. */
    public val ibisReason: String get() =
        CameraControlsContext.context.getString(R.string.cc_gyro_ibis_reason)

    /** The lens-side stabilization hint. */
    public val ibisLens: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_ibis_lens)

    /** The arming sheet's confirm button. */
    public val ibisTurnOff: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_ibis_turn_off)

    /** The arming sheet's keep-stabilization button. */
    public val ibisKeep: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_ibis_keep)

    // Calibration

    /** The overline above the intro, complete and invalidated mount screens. */
    public val phoneMount: String get()= CameraControlsContext.context.getString(R.string.cc_phone_mount)

    /** The calibration screen's title. */
    public val calibrateTitle: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_calibrate_title)

    /** How long calibration takes and what to do with the phone. */
    public val calibrateBody: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_calibrate_body)

    /** The calibration screen's start button. */
    public val getStarted: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_get_started)

    /** The calibration screen's saved-calibration button. */
    public val useLastCalibration: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_use_last_calibration)

    /** The shared cancel button. */
    public val cancel: String get()= CameraControlsContext.context.getString(R.string.cc_cancel)

    /** The instruction for ring step [step]: 0 level, 1 tilt up, 2 tilt and lean. */
    public fun step(step: Int): String = when (step) {
        0 -> CameraControlsContext.context.getString(R.string.cc_gyro_step_level)
        1 -> CameraControlsContext.context.getString(R.string.cc_gyro_step_tilt_up)
        else -> CameraControlsContext.context.getString(R.string.cc_gyro_step_tilt_lean)
    }

    /** The step counter, for example "Step 2 of 3". */
    public fun stepCounter(n: Int): String = CameraControlsContext.context.getString(R.string.cc_gyro_step_counter, n)

    /** The hint under the filling ring. */
    public val holdHint: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_hold_hint)

    /** Why the ring cannot accept a reading on the current step. */
    public fun block(reason: GyroCalibrationBlock): String = when (reason) {
        GyroCalibrationBlock.CAMERA_DISCONNECTED -> CameraControlsContext.context.getString(R.string.cc_gyro_block_disconnected)
        GyroCalibrationBlock.RECORDING -> CameraControlsContext.context.getString(R.string.cc_gyro_block_recording)
        GyroCalibrationBlock.MOVING -> CameraControlsContext.context.getString(R.string.cc_gyro_block_moving)
        GyroCalibrationBlock.WAITING_FOR_CAMERA -> CameraControlsContext.context.getString(R.string.cc_gyro_block_waiting)
    }

    /** Why the ring did not line up, on the retry screen. */
    public val retry: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_retry)

    /** Why an in-progress ring calibration was discarded. */
    public fun invalidation(reason: GyroCalibrationInvalidation): String = when (reason) {
        GyroCalibrationInvalidation.CAMERA_CHANGED -> CameraControlsContext.context.getString(R.string.cc_gyro_invalid_camera_changed)
        GyroCalibrationInvalidation.RECORDING_STARTED ->
            CameraControlsContext.context.getString(R.string.cc_gyro_invalid_recording)
        GyroCalibrationInvalidation.CONNECTION_ENDED ->
            CameraControlsContext.context.getString(R.string.cc_gyro_invalid_disconnected)
    }

    /** The completion screen's title. */
    public val completeTitle: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_complete_title)

    /** The completion screen's body. */
    public val completeBody: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_complete_body)

    /** The completion screen's button. */
    public val done: String get()= CameraControlsContext.context.getString(R.string.cc_done)

    /** The ring's hold progress, for example "Holding, 40 percent". */
    public fun holdProgressA11y(percent: Int): String = CameraControlsContext.context.getString(R.string.cc_gyro_holding_percent, percent)

    /** One finished step, for example "Step 1 done". */
    public fun stepDoneA11y(n: Int): String = CameraControlsContext.context.getString(R.string.cc_gyro_step_done, n)

    // Drift

    /** The drift popup's title. */
    public val driftTitle: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_drift_title)

    /** The drift popup's question. */
    public val driftQuestion: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_drift_question)

    /** The drift popup's confirm button. */
    public val recalibrate: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_recalibrate)

    /** The drift popup's dismiss button. */
    public val notNow: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_not_now)

    // Logs

    /** The gyro logs screen's title. */
    public val logsTitle: String get()= CameraControlsContext.context.getString(R.string.cc_gyro_logs)

    /** What to do with a shared log. */
    public val tip: String get() =
        CameraControlsContext.context.getString(R.string.cc_gyro_tip)

    /** The VoiceOver and TalkBack label of the tip's close button. */
    public val dismissTip: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_dismiss_tip)

    /** The Today section title. */
    public val today: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_today)

    /** The Yesterday section title. */
    public val yesterday: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_yesterday)

    /** The selection mode's button. */
    public val select: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_select)

    /** The selection button, for example "Share (2)". */
    public fun shareCount(n: Int): String = CameraControlsContext.context.getString(R.string.cc_gyro_share_count, n)

    /** One row's share button. */
    public val shareLog: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_share_log)

    /** The separator between the parts of a row or take subtitle (U+00B7 with spaces). */
    public const val separator: String = " · "

    /** The one status badge of a take row. */
    public fun status(s: GyroTakeStatus): String = when (s) {
        GyroTakeStatus.Recording -> "REC"
        is GyroTakeStatus.MountMoved -> CameraControlsContext.context.getString(R.string.cc_gyro_mount_moved_at, length(s.offsetSeconds))
        GyroTakeStatus.CutShort -> CameraControlsContext.context.getString(R.string.cc_gyro_cut_short)
        GyroTakeStatus.SmallGaps -> CameraControlsContext.context.getString(R.string.cc_gyro_small_gaps)
        GyroTakeStatus.NotCalibrated -> CameraControlsContext.context.getString(R.string.cc_gyro_not_calibrated)
        GyroTakeStatus.Ready -> CameraControlsContext.context.getString(R.string.cc_gyro_ready)
    }

    /** The take page's verdict line. */
    public fun verdict(s: GyroTakeStatus): String = when (s) {
        GyroTakeStatus.Recording -> CameraControlsContext.context.getString(R.string.cc_gyro_still_recording)
        GyroTakeStatus.Ready -> CameraControlsContext.context.getString(R.string.cc_gyro_ready_gyroflow)
        else -> status(s)
    }

    /** The delete confirmation's title. */
    public val deleteTitle: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_delete_title)

    /** The delete confirmation's button. */
    public val delete: String get()= CameraControlsContext.context.getString(R.string.cc_delete)

    /** The empty logs screen's title. */
    public val emptyTitle: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_no_logs_title)

    /** The empty logs screen's body. */
    public val emptyBody: String get() =
        CameraControlsContext.context.getString(R.string.cc_gyro_empty_body)

    /** The empty logs screen's button. */
    public val turnOnGyro: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_turn_on)

    /** A take row's calibration badge when a verified mount is stored. */
    public val calibratedMount: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_calibrated_mount)

    /** A take row's calibration badge when no verified mount is stored. */
    public val notCalibrated: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_not_calibrated_badge)

    // Take details

    /** The Details section's title. */
    public val details: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_details)

    /** The start offset row's label. */
    public val startsLabel: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_starts)

    /** The smoothness row's label. */
    public val smoothnessLabel: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_smoothness)

    /** The sample rate row's label. */
    public val sampleRateLabel: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_sample_rate)

    /** How far the log starts before (positive) or after (negative) REC, to two decimals. */
    public fun startOffset(seconds: Double): String {
        val value = String.format(Locale.ROOT, "%.2f", abs(seconds))
        return if (seconds >= 0) CameraControlsContext.context.getString(R.string.cc_gyro_s_before_rec, value) else CameraControlsContext.context.getString(R.string.cc_gyro_s_after_rec, value)
    }

    /** The Details smoothness row. A recording take has no verdict yet. */
    public fun smoothness(s: GyroTakeStatus): String = when (s) {
        GyroTakeStatus.Ready, GyroTakeStatus.NotCalibrated, is GyroTakeStatus.MountMoved -> CameraControlsContext.context.getString(R.string.cc_gyro_no_gaps)
        GyroTakeStatus.SmallGaps -> CameraControlsContext.context.getString(R.string.cc_gyro_small_gaps)
        GyroTakeStatus.CutShort -> CameraControlsContext.context.getString(R.string.cc_gyro_cut_short)
        GyroTakeStatus.Recording -> CameraControlsContext.context.getString(R.string.cc_gyro_still_recording)
    }

    /** The Details sample rate, rounded to whole hertz, for example "200 Hz". */
    public fun sampleRate(hz: Double): String = (if (hz.isFinite()) hz.roundToInt() else 0).toString() + " Hz"

    /**
     * `m:ss`: whole seconds (truncated), minutes unbounded, seconds zero-padded; negative and
     * non-finite values read as zero.
     */
    public fun length(seconds: Double): String {
        val total = if (seconds.isFinite()) maxOf(0, seconds.toInt()) else 0
        val secs = total % 60
        return (total / 60).toString() + ":" + (if (secs < 10) "0" else "") + secs
    }

    // Notices

    /** The notice line for a capture problem. */
    public fun notice(n: GyroNotice): String = when (n) {
        GyroNotice.TURN_OFF_FAILED -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_turn_off_failed)
        GyroNotice.RESTORE_FAILED -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_restore_failed)
        GyroNotice.SENSOR_UNAVAILABLE -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_sensor_unavailable)
        GyroNotice.SENSOR_STALLED -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_sensor_stalled)
        GyroNotice.SAMPLES_LOST -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_samples_lost)
        GyroNotice.RECORDING_EVENTS_LOST ->
            CameraControlsContext.context.getString(R.string.cc_gyro_notice_events_lost)
        GyroNotice.SAVE_FAILED -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_save_failed)
        GyroNotice.OPEN_FAILED -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_open_failed)
        GyroNotice.SHARE_FAILED -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_share_failed)
        GyroNotice.DELETE_FAILED -> CameraControlsContext.context.getString(R.string.cc_gyro_notice_delete_failed)
        GyroNotice.RECOVERY_INCOMPLETE ->
            CameraControlsContext.context.getString(R.string.cc_gyro_notice_recovery)
    }

    // Settings

    /** The gyro settings row that opens calibration. */
    public val settingsCalibrate: String get() = CameraControlsContext.context.getString(R.string.cc_gyro_settings_calibrate)
}