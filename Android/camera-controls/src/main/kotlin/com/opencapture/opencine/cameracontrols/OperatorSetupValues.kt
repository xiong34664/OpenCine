package com.opencapture.opencine.cameracontrols

import android.content.Context
import com.opencapture.monitorui.MonitorLinkHealth
import com.opencapture.opencine.vocabulary.ConnectionPhase
import com.opencapture.opencine.vocabulary.LutLookResolver
import com.opencapture.opencine.vocabulary.MediaClipFormatting
import java.io.File
import java.util.Locale

/** Live link health for the settings link tile and the FPS chip. */
object OperatorLinkHealth {
    const val TARGET_FPS = 25.0

    fun bars(
        isLive: Boolean,
        videoPackets: Int,
        hasVideoFormat: Boolean,
        measuredFps: Double = -1.0,
    ): Int {
        if (!isLive) return 0
        if (measuredFps > 0.0) {
            val score = ((measuredFps / TARGET_FPS) * 100.0).coerceIn(0.0, 100.0)
            val rounded = kotlin.math.round(score).toInt()
            if (rounded <= 0) return 1
            return ((rounded + 24) / 25).coerceIn(1, 4)
        }
        return when {
            hasVideoFormat && videoPackets > 0 -> 4
            videoPackets >= 400 -> 4
            videoPackets >= 120 -> 3
            videoPackets >= 1 -> 2
            else -> 1
        }
    }

    fun score(bars: Int): Int = MonitorLinkHealth.score(bars)

    fun caption(isLive: Boolean, bars: Int): String {
        if (!isLive) return CameraControlsContext.context.getString(R.string.cc_no_live_path)
        if (bars <= 0) return CameraControlsContext.context.getString(R.string.cc_waiting_for_link)
        return when (MonitorLinkHealth.band(score(bars))) {
            MonitorLinkHealth.Band.STABLE -> CameraControlsContext.context.getString(R.string.cc_link_clean_stable)
            MonitorLinkHealth.Band.WATCH -> CameraControlsContext.context.getString(R.string.cc_link_some_loss)
            MonitorLinkHealth.Band.POOR -> CameraControlsContext.context.getString(R.string.cc_link_weak)
        }
    }

    @Deprecated("Use MonitorLinkMeter.compactFPS")
    fun compactFps(incoming: String): String = MonitorLinkMeter.compactFPS(incoming)

    @Deprecated("Use MonitorLinkMeter.formatMeasuredFPS")
    fun formatMeasuredFps(fps: Double): String = MonitorLinkMeter.formatMeasuredFPS(fps)

    @Deprecated("Use MonitorLinkMeter.chipLabel")
    fun fpsChipLabel(isLive: Boolean, recovering: Boolean, measuredFps: Double,
        phase: ConnectionPhase): String {
        val label = MonitorLinkMeter.chipLabel(phase, recovering, false,
            MonitorLinkMeter.compactFPS(MonitorLinkMeter.formatMeasuredFPS(measuredFps)), measuredFps)
        return if (isLive && phase == ConnectionPhase.IDLE && !recovering && measuredFps <= 0.0) "LINK" else label
    }

    fun liveTileDetail(
        isLive: Boolean,
        cameraName: String,
        fpsLabel: String,
        phaseLabel: String,
    ): String = if (isLive) "$cameraName · BLE + Wi-Fi · $fpsLabel FPS" else phaseLabel
}

fun connectionPhaseLabel(phase: ConnectionPhase, failure: String?): String =
    when (phase) {
        ConnectionPhase.IDLE -> CameraControlsContext.context.getString(R.string.cc_idle)
        ConnectionPhase.SCANNING -> CameraControlsContext.context.getString(R.string.cc_scanning_for_camera)
        ConnectionPhase.CONNECTING_GATT -> CameraControlsContext.context.getString(R.string.cc_connecting_bluetooth)
        ConnectionPhase.PAIRING -> CameraControlsContext.context.getString(R.string.cc_pairing)
        ConnectionPhase.AWAITING_APPROVAL -> CameraControlsContext.context.getString(R.string.cc_approve_on_camera)
        ConnectionPhase.READING_WIFI_CREDS -> CameraControlsContext.context.getString(R.string.cc_reading_wifi_creds)
        ConnectionPhase.JOINING_WIFI -> CameraControlsContext.context.getString(R.string.cc_joining_camera_wifi)
        ConnectionPhase.OPENING_DATALINK -> CameraControlsContext.context.getString(R.string.cc_opening_datalink)
        ConnectionPhase.LIVE -> CameraControlsContext.context.getString(R.string.cc_connected)
        ConnectionPhase.FAILED ->
            if (failure.isNullOrBlank()) CameraControlsContext.context.getString(R.string.cc_failed)
            else CameraControlsContext.context.getString(R.string.cc_failed_with, failure)
    }

/** The assists a clean view can keep pinned, by stored key. */
enum class CleanPinTool(val key: String, val title: String) {
    LUT("LUT", "LUT"),
    PEAKING("PEAK", "Peaking"),
    FALSE_COLOR("FALSE", "False Color"),
    ZEBRA("ZEBRA", "Zebra"),
    WAVEFORM("WAVE", "Waveform"),
    PARADE("PARADE", "Parade"),
    HISTOGRAM("HISTO", "Histogram"),
    VECTORSCOPE("VECTOR", "Vectorscope"),
    TRAFFIC_LIGHTS("LIGHTS", "Traffic Lights"),
    ND("ND", "ND Suggestion"),
    GUIDES("GUIDES", "Guides"),
    GRID("GRID", "Grid"),
    CROSSHAIR("CROSS", "Crosshair"),
    DESQUEEZE("DESQ", "Anamorphic Desqueeze"),
    MIRROR("MIRROR", "Mirror"),
    AUDIO("AUDIO", "Audio Levels"),
}

/** The cache size line: the shared decimal byte label, "Empty" for nothing cached. */
fun formatCacheSize(bytes: Long): String = if (bytes <= 0L) CameraControlsContext.context.getString(R.string.cc_empty) else MediaClipFormatting.byteLabel(bytes)

fun formatAppVersion(versionName: String, versionCode: Long): String = "$versionName ($versionCode)"

/** The operator setup help copy both apps show; [appName] fills the lines that name the app. */
@Suppress("PropertyName")
open class OperatorHelpCopy(private val appName: String) {
    val CURRENT_TRANSPORT get() = CameraControlsContext.context.getString(R.string.cc_current_transport)
    val PHASE get() = CameraControlsContext.context.getString(R.string.cc_phase_handshake)
    val CAMERA_WIFI get() = CameraControlsContext.context.getString(R.string.cc_camera_wifi_help)
    val SAVED_CAMERAS get() = CameraControlsContext.context.getString(R.string.cc_saved_cameras_help)
    val EDIT_VIEW get() = CameraControlsContext.context.getString(R.string.cc_edit_view_help)
    val FRAME_IO get() = CameraControlsContext.context.getString(R.string.cc_frame_io_help)
    val RECORD_CONFIRMATION get() = CameraControlsContext.context.getString(R.string.cc_record_confirmation_help)
    val SHOOTING_MODE get() = CameraControlsContext.context.getString(R.string.cc_shooting_mode_help)
    val HAPTICS get() = CameraControlsContext.context.getString(R.string.cc_haptics_help)
    val JOYSTICK_SENSITIVITY get() = CameraControlsContext.context.getString(R.string.cc_joystick_sensitivity_help)
    val VIRTUAL_JOYSTICK_SIZE get() = CameraControlsContext.context.getString(R.string.cc_virtual_joystick_size_help)
    val VIRTUAL_JOYSTICK_INVERT_PAN get() = CameraControlsContext.context.getString(R.string.cc_virtual_joystick_invert_pan_help)
    val VIRTUAL_JOYSTICK_INVERT_TILT get() = CameraControlsContext.context.getString(R.string.cc_virtual_joystick_invert_tilt_help)
    val VIRTUAL_JOYSTICK_DEADZONE get() = CameraControlsContext.context.getString(R.string.cc_virtual_joystick_deadzone_help)
    val VIRTUAL_JOYSTICK_RESPONSE get() = CameraControlsContext.context.getString(R.string.cc_virtual_joystick_response_help)
    val GIMBAL_JOYSTICK get() = CameraControlsContext.context.getString(R.string.cc_gimbal_joystick_help)
    val GAMEPAD get() = CameraControlsContext.context.getString(R.string.cc_gamepad_help)
    val KEEP_SCREEN_AWAKE get() = MonitorSettingsCopy.keepScreenAwake
    val HDR_DISPLAY get() = CameraControlsContext.context.getString(R.string.cc_hdr_display_help)
    val THEME get() = CameraControlsContext.context.getString(R.string.cc_theme_help)
    val SUPPORT get() = CameraControlsContext.context.getString(R.string.cc_support_help)
    val REPORT get() = CameraControlsContext.context.getString(R.string.cc_report_help)
    val REPORT_PROBLEM get() = CameraControlsContext.context.getString(R.string.cc_report_problem_help)
    val RELIABILITY_REPORTS get() = MonitorSettingsCopy.automaticReports
    val RELIABILITY_UNAVAILABLE get() = CameraControlsContext.context.getString(R.string.cc_reliability_unavailable_help)
    val FEATURE get() = CameraControlsContext.context.getString(R.string.cc_feature_help)
    val SOURCE get() = CameraControlsContext.context.getString(R.string.cc_source_help, appName)
    val LINK_HEALTH get() = CameraControlsContext.context.getString(R.string.cc_link_health_help)
    val CLEAR_CACHE get() = CameraControlsContext.context.getString(R.string.cc_clear_cache_help)
    val LUT_LOOK get() = CameraControlsContext.context.getString(R.string.cc_lut_look_help)
    val APP_VERSION get() = CameraControlsContext.context.getString(R.string.cc_app_version_help, appName)
    val LOCAL_CACHE get() = CameraControlsContext.context.getString(R.string.cc_local_cache_help)
    val CACHE_FULL_RESOLUTION get() = CameraControlsContext.context.getString(R.string.cc_cache_full_resolution_help)
    val FEED_UPSCALER get() = MonitorSettingsCopy.feedUpscaler
    val FALSE_COLOR_SCALE get() = CameraControlsContext.context.getString(R.string.cc_false_color_scale_help)
    val FALSE_COLOR_REFERENCE get() = CameraControlsContext.context.getString(R.string.cc_false_color_reference_help)
    val PEAKING_SENSITIVITY get() = CameraControlsContext.context.getString(R.string.cc_peaking_sensitivity_help)
    val PEAKING_COLOR get() = CameraControlsContext.context.getString(R.string.cc_peaking_color_help)
    val ZEBRA_UNITS get() = CameraControlsContext.context.getString(R.string.cc_zebra_units_help)
    val ZEBRA_HIGHLIGHT get() = CameraControlsContext.context.getString(R.string.cc_zebra_highlight_help)
    val ZEBRA_MIDTONE get() = CameraControlsContext.context.getString(R.string.cc_zebra_midtone_help)
    val WAVEFORM_BRIGHTNESS get() = CameraControlsContext.context.getString(R.string.cc_waveform_brightness_help)
    val PARADE_BRIGHTNESS get() = CameraControlsContext.context.getString(R.string.cc_parade_brightness_help)
    val VECTORSCOPE_ZOOM get() = CameraControlsContext.context.getString(R.string.cc_vectorscope_zoom_help)
    val VECTORSCOPE_BRIGHTNESS get() = CameraControlsContext.context.getString(R.string.cc_vectorscope_brightness_help)
    val TRAFFIC_LIGHTS_COMPENSATION get() = CameraControlsContext.context.getString(R.string.cc_traffic_lights_compensation_help)
}

/**
 * The clip cache directories: the app's [mediaRoots] (per-camera folders, each keeping its `index.json`)
 * first, then the older flat caches.
 */
open class OperatorMediaCacheDirs(private val mediaRoots: (Context) -> List<File>) {
    fun candidates(context: Context): List<File> =
        mediaRoots(context) + listOf(
            File(context.filesDir, "media-cache"),
            File(context.cacheDir, "media-cache"),
        )

    fun existingDir(context: Context): File? = candidates(context).firstOrNull { it.isDirectory }

    fun byteCount(context: Context): Long {
        val dir = existingDir(context) ?: return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    fun clear(context: Context) {
        val mediaRoot = mediaRoots(context).firstOrNull { it.isDirectory }
        if (mediaRoot != null) {
            mediaRoot.listFiles()?.forEach { cameraDir ->
                if (!cameraDir.isDirectory) return@forEach
                cameraDir.listFiles()?.forEach { child ->
                    if (child.name != "index.json") child.deleteRecursively()
                }
            }
            return
        }
        existingDir(context)?.listFiles()?.forEach { it.deleteRecursively() }
    }
}

fun lutLookLabel(
    selection: String,
    enabled: Boolean = true,
    colorMode: Int = -1,
    profile: com.opencapture.opencine.vocabulary.CameraLUTProfile? = null,
): String {
    val source =
        LutLookResolver.resolve(
            selection = selection,
            lutOn = true,
            colorMode = colorMode,
            profile = profile,
        )
    return LutLookResolver.statusLabel(enabled, selection, source)
}
