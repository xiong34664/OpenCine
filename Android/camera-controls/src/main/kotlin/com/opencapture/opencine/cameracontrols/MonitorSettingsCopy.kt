package com.opencapture.opencine.cameracontrols

/** Operator help shared by the settings compositions (Swift `MonitorSettingsCopy`). */
public object MonitorSettingsCopy {
    /** The Feed Upscaler help on the Link tab's Processing card, in every app (A2-78). */
    public val feedUpscaler: String
        get() = CameraControlsContext.context.getString(R.string.cc_feed_upscaler_help)

    public val keepScreenAwake: String
        get() = CameraControlsContext.context.getString(R.string.cc_keep_screen_awake_help)

    /**
     * Automatic reports, one disclosure on every platform (A2-37). It states what the shared
     * reporter sends: crashes and hangs, live-feed reports and session health counts, with no
     * images, footage or location.
     */
    public val automaticReports: String
        get() = CameraControlsContext.context.getString(R.string.cc_automatic_reports_help)

    /**
     * Shown in place of the false colour and zebra settings while the camera has not named its colour
     * curve (product ruling 2026-10-16). Swift twin: `MonitorSettingsCopy.sourceCurveUnknown`.
     */
    public val sourceCurveUnknown: String
        get() = CameraControlsContext.context.getString(R.string.cc_source_curve_unknown)

    /**
     * The reliability-report paragraph of every app's bundled privacy text, the same facts as the
     * websites: the random install identifier made on opt-in and deleted on opt-out, and the toggle's place.
     */
    public val reliabilityReportsPrivacy: String
        get() = CameraControlsContext.context.getString(R.string.cc_reliability_reports_privacy)
}
