package com.opencapture.monitorui.settings

import com.opencapture.monitorui.R

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** Common touch safety choices with storage and optional domain copy supplied by the host. */
@Composable
public fun MonitorOperatorSafetySettings(
    recordConfirmation: Boolean, haptics: Boolean, keepScreenAwake: Boolean,
    onRecordConfirmation: (Boolean) -> Unit, onHaptics: (Boolean) -> Unit,
    onKeepScreenAwake: (Boolean) -> Unit,
    hapticsHelp: String = stringResource(R.string.mui_haptics_help),
    keepScreenAwakeHelp: String = stringResource(R.string.mui_keep_screen_awake_help),
    feedback: () -> Unit = {},
) {
    MonitorSettingsRowCard(title = stringResource(R.string.mui_touch_safety)) {
        MonitorSettingsSwitchInlineRow(
            title = stringResource(R.string.mui_record_confirmation), help = stringResource(R.string.mui_record_confirmation_help),
            showTopDivider = false, isOn = recordConfirmation,
        ) { feedback(); onRecordConfirmation(!recordConfirmation) }
        MonitorSettingsSwitchInlineRow(title = stringResource(R.string.mui_haptics), help = hapticsHelp, isOn = haptics) {
            feedback(); onHaptics(!haptics)
        }
        MonitorSettingsSwitchInlineRow(title = stringResource(R.string.mui_keep_screen_awake), help = keepScreenAwakeHelp, isOn = keepScreenAwake) {
            feedback(); onKeepScreenAwake(!keepScreenAwake)
        }
    }
}

/** A display preference, independently of camera capture settings. */
@Composable
public fun MonitorPanelSettings(hdrDisplay: Boolean, help: String,
    onChange: (Boolean) -> Unit, feedback: () -> Unit = {}) {
    MonitorSettingsRowCard(title = stringResource(R.string.mui_panel)) {
        MonitorSettingsSwitchInlineRow(title = stringResource(R.string.mui_hdr_display), help = help, showTopDivider = false, isOn = hdrDisplay) {
            feedback(); onChange(!hdrDisplay)
        }
    }
}
