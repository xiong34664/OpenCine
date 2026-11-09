package com.opencapture.monitorui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.opencapture.opencine.media.MonitorCrushClipCompensation
import com.opencapture.monitorui.settings.MonitorSettingsCrushClipSegmented
import com.opencapture.monitorui.settings.MonitorSettingsInlineRow
import com.opencapture.monitorui.settings.MonitorSettingsSwitchInlineRow

@Composable
fun MonitorHistogramMenu(trafficLights: Boolean, onTrafficLights: () -> Unit,
    compensation: MonitorCrushClipCompensation, onCompensation: (MonitorCrushClipCompensation) -> Unit,
    trafficHelp: String, compensationHelp: String) {
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_traffic_lights), isOn = trafficLights, help = trafficHelp,
        showTopDivider = false, onToggle = onTrafficLights)
    MonitorCrushClipMenu(compensation, onCompensation, compensationHelp)
}

@Composable
fun MonitorCrushClipMenu(compensation: MonitorCrushClipCompensation,
    onCompensation: (MonitorCrushClipCompensation) -> Unit, help: String, showTopDivider: Boolean = true) {
    MonitorSettingsInlineRow(stringResource(R.string.mui_crush_clip_compensation), help, showTopDivider = showTopDivider, stacked = true) {
        MonitorSettingsCrushClipSegmented(
            options = MonitorCrushClipCompensation.entries.map { it.label to it.compactLabel },
            selectedLabel = compensation.label, accentSelection = true,
        ) { label ->
            MonitorCrushClipCompensation.entries.firstOrNull { it.label == label }?.let(onCompensation)
        }
    }
}
