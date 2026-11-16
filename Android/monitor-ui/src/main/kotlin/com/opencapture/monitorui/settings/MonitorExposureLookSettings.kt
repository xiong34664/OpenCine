package com.opencapture.monitorui.settings

import com.opencapture.monitorui.R

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/** Local exposure-meter presentation. The host supplies its exercised reading policy and action. */
@Composable
fun MonitorExposureMeterSettings(isOn: Boolean, help: String, onToggle: () -> Unit): Unit {
    MonitorSettingsRowCard(title = stringResource(R.string.mui_ev_meter)) {
        MonitorSettingsSwitchInlineRow(title = stringResource(R.string.mui_enabled), isOn = isOn, help = help,
            showTopDivider = false, onToggle = onToggle)
    }
}

/** A known selected look and a local picker action. Catalogs and preparation remain source-owned. */
@Composable
fun MonitorLookSettings(look: String, help: String, onOpen: () -> Unit): Unit {
    MonitorSettingsRowCard(title = "LUT") {
        MonitorSettingsInlineRow(title = stringResource(R.string.mui_look), help = help, showTopDivider = false) {
            MonitorSettingsValueText(value = look)
        }
        MonitorSettingsInlineRow(title = stringResource(R.string.mui_choose_look)) {
            MonitorSettingsActionPill(title = stringResource(R.string.mui_open), onClick = onOpen)
        }
    }
}
