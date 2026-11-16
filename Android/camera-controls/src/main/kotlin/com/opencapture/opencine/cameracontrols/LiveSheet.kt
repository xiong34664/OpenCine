package com.opencapture.opencine.cameracontrols


enum class LiveSheet {
    ISO,
    SHUTTER,
    WB,
    FOCUS,
    /** Action 6 aperture strategy, in the FOCUS slot. */
    APERTURE,
    /** Capture-bar MODE: expo Auto/Manual. Not shooting Video/Photo. */
    EXPO,
    AUDIO,
    COLOR,
    FORMAT,
    /** Top-deck shooting Video/Photo/… Not capture-bar EXPO Auto/Manual. */
    MODE,
}

val LiveSheet.isRecordingSetup: Boolean
    get() = this == LiveSheet.FORMAT || this == LiveSheet.COLOR

val LiveSheet.isTopAnchored: Boolean
    get() = isRecordingSetup || this == LiveSheet.MODE

/** Lower ISO/WB/… stay visible for top FORMAT/COLOR/MODE tap or hold. */
fun hidesLowerCaptureValues(
    sheet: LiveSheet?,
    stripQuick: Boolean,
    topQuick: Boolean,
): Boolean {
    if (topQuick) return false
    if (sheet?.isTopAnchored == true) return false
    return sheet != null || stripQuick
}

val LiveSheet.headerLabel: String
    get() =
        when (this) {
            LiveSheet.ISO -> "ISO"
            LiveSheet.SHUTTER -> "SHUTTER"
            LiveSheet.WB -> CameraControlsContext.context.getString(R.string.cc_white_balance_caps)
            LiveSheet.FOCUS -> "FOCUS"
            LiveSheet.APERTURE -> "APERTURE"
            LiveSheet.EXPO -> CameraControlsContext.context.getString(R.string.cc_exposure_mode)
            LiveSheet.AUDIO -> "AUDIO"
            LiveSheet.COLOR -> "COLOR"
            LiveSheet.FORMAT -> "RESOLUTION"
            LiveSheet.MODE -> CameraControlsContext.context.getString(R.string.cc_shooting_mode_caps)
        }

val LiveSheet.subtitle: String
    get() =
        when (this) {
            LiveSheet.ISO -> CameraControlsContext.context.getString(R.string.cc_sensitivity)
            LiveSheet.SHUTTER -> CameraControlsContext.context.getString(R.string.cc_shutter_angle_speed)
            LiveSheet.WB -> CameraControlsContext.context.getString(R.string.cc_kelvin_auto_tint)
            LiveSheet.FOCUS -> "AF-S / AF-C"
            LiveSheet.APERTURE -> CameraControlsContext.context.getString(R.string.cc_aperture_strategy)
            LiveSheet.EXPO -> CameraControlsContext.context.getString(R.string.cc_exposure)
            LiveSheet.AUDIO -> CameraControlsContext.context.getString(R.string.cc_audio_tabs_subtitle)
            LiveSheet.COLOR -> CameraControlsContext.context.getString(R.string.cc_color_mode)
            LiveSheet.FORMAT -> CameraControlsContext.context.getString(R.string.cc_frame_rate)
            LiveSheet.MODE -> CameraControlsContext.context.getString(R.string.cc_shooting_mode)
        }