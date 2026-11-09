package com.opencapture.opencine.appkit

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.opencapture.monitorui.MonitorAssistTool
import com.opencapture.monitorui.settings.MonitorDisplaySectionStyle
import com.opencapture.monitorui.settings.MonitorDisplaySections
import com.opencapture.monitorui.settings.MonitorDisplayToggle
import com.opencapture.monitorui.settings.MonitorDisplaySection as DisplaySection

/** Common display composition over existing, app-namespaced preference values, one section per
 * DISP mode the source offers. */
@Composable
public fun MonitorOperatorDisplaySections(
    modes: List<MonitorDisplayMode>,
    chrome: (MonitorDisplayMode) -> MonitorDisplayChrome,
    assist: MonitorAssistState,
    onChange: (MonitorDisplayMode, MonitorDisplayChrome) -> Unit,
    available: Set<MonitorDisplaySection> = MonitorDisplaySection.entries.toSet(),
    pinnable: Set<MonitorAssistTool> = MonitorAssistTool.cleanPinCases.toSet(),
    onPinsChanged: ((Set<MonitorAssistTool>) -> Unit)? = null,
    edit: ((MonitorDisplayMode) -> Unit)? = null,
    feedback: () -> Unit = {},
): Unit {
    val order = listOf(MonitorDisplaySection.STATUS_BAR, MonitorDisplaySection.TOOL_BAR,
        MonitorDisplaySection.CAMERA_VALUES, MonitorDisplaySection.LOCK_BUTTON, MonitorDisplaySection.BATTERIES,
        MonitorDisplaySection.REC_READOUT, MonitorDisplaySection.TIMECODE, MonitorDisplaySection.FORMAT,
        MonitorDisplaySection.CODEC, MonitorDisplaySection.COLOR, MonitorDisplaySection.STORAGE, MonitorDisplaySection.FPS,
        MonitorDisplaySection.RAIL_RECORD, MonitorDisplaySection.RAIL_MEDIA, MonitorDisplaySection.RAIL_SETTINGS,
        MonitorDisplaySection.ZOOM_CHIP, MonitorDisplaySection.GIMBAL_STICK, MonitorDisplaySection.FOCUS_BOX)
    MonitorDisplaySections(modes.map { mode ->
        val value = chrome(mode)
        DisplaySection(id = mode.name, title = mode.settingsTitle, caption = mode.settingsCaption,
            reset = {
                feedback()
                onChange(mode, MonitorDisplayChrome.defaults(mode))
            }, editHint = stringResource(R.string.appkit_arrange_monitor_hint),
            edit = edit?.let { action -> { action(mode) } },
            unavailableEditHelp = stringResource(R.string.appkit_connect_to_arrange),
            visibility = order.filter { it in available && MonitorDisplayChrome.isConfigurable(it, mode) }.map { section ->
                MonitorDisplayToggle(id = mode.name + "." + section.name,
                    title = if (section == MonitorDisplaySection.FOCUS_BOX) AppkitContext.context.getString(R.string.appkit_af_box) else section.title,
                    isOn = value.isVisible(section), help = displayHelp(section)) {
                    feedback(); onChange(mode, value.toggling(section))
                }
            }, pinsTitle = stringResource(R.string.appkit_view_assists_stay_clean),
            pins = if (mode != MonitorDisplayMode.CLEAN) emptyList() else MonitorAssistTool.cleanPinCases.filter { it in pinnable }.map { tool ->
                MonitorDisplayToggle(id = "clean.pin." + tool.name, title = tool.title,
                    isOn = tool in assist.pinned, accessibilityLabel = stringResource(R.string.appkit_keep_assist_clean, tool.title)) {
                    feedback()
                    if (onPinsChanged == null) assist.togglePin(tool)
                    else {
                        val next = if (tool in assist.pinned) assist.pinned - tool else assist.pinned + tool
                        assist.pinned = next; onPinsChanged(next)
                    }
                }
            }, style = MonitorDisplaySectionStyle.CONTROLS)
    })
}

private fun displayHelp(section: MonitorDisplaySection): String = when (section) {
    MonitorDisplaySection.STATUS_BAR -> AppkitContext.context.getString(R.string.appkit_help_status_bar)
    MonitorDisplaySection.TOOL_BAR -> AppkitContext.context.getString(R.string.appkit_help_tool_bar)
    MonitorDisplaySection.CAMERA_VALUES -> AppkitContext.context.getString(R.string.appkit_help_camera_values)
    MonitorDisplaySection.LOCK_BUTTON -> AppkitContext.context.getString(R.string.appkit_help_lock_button)
    MonitorDisplaySection.BATTERIES -> AppkitContext.context.getString(R.string.appkit_help_batteries)
    MonitorDisplaySection.REC_READOUT -> AppkitContext.context.getString(R.string.appkit_help_rec_readout)
    MonitorDisplaySection.TIMECODE -> AppkitContext.context.getString(R.string.appkit_help_timecode)
    MonitorDisplaySection.FORMAT -> AppkitContext.context.getString(R.string.appkit_help_format)
    MonitorDisplaySection.CODEC -> AppkitContext.context.getString(R.string.appkit_help_codec)
    MonitorDisplaySection.COLOR -> AppkitContext.context.getString(R.string.appkit_help_color)
    MonitorDisplaySection.STORAGE -> AppkitContext.context.getString(R.string.appkit_help_storage)
    MonitorDisplaySection.FPS -> AppkitContext.context.getString(R.string.appkit_help_fps)
    MonitorDisplaySection.RAIL_RECORD -> AppkitContext.context.getString(R.string.appkit_help_rail_record)
    MonitorDisplaySection.RAIL_MEDIA -> AppkitContext.context.getString(R.string.appkit_help_rail_media)
    MonitorDisplaySection.RAIL_SETTINGS -> AppkitContext.context.getString(R.string.appkit_help_rail_settings)
    MonitorDisplaySection.ZOOM_CHIP -> AppkitContext.context.getString(R.string.appkit_help_zoom_chip)
    MonitorDisplaySection.GIMBAL_STICK -> AppkitContext.context.getString(R.string.appkit_help_gimbal_stick)
    MonitorDisplaySection.FOCUS_BOX -> AppkitContext.context.getString(R.string.appkit_help_focus_box)
}
