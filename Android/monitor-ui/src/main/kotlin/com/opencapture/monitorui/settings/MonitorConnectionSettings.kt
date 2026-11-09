package com.opencapture.monitorui.settings

import com.opencapture.monitorui.R

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.res.stringResource

/** Source-interpreted connection facts; no transport or credential storage ownership. */
@Composable
fun MonitorConnectionSettings(
    transport: String, transportHelp: String, phase: String, phaseHelp: String,
    network: String? = null, networkHelp: String = "",
) {
    MonitorSettingsRowCard(title = stringResource(R.string.mui_connection)) {
        MonitorSettingsInlineRow(title = stringResource(R.string.mui_current_transport), help = transportHelp, showTopDivider = false) {
            MonitorSettingsValueText(transport)
        }
        MonitorSettingsInlineRow(title = stringResource(R.string.mui_phase), help = phaseHelp) { MonitorSettingsValueText(phase) }
        if (network != null) {
            MonitorSettingsInlineRow(title = stringResource(R.string.mui_camera_wifi), help = networkHelp) { MonitorSettingsValueText(network) }
        }
    }
}

data class MonitorSavedCameraSummary(val id: String, val name: String, val detail: String, val network: String? = null)

/** Read-only summaries; pairing remains the host's cameras flow. */
@Composable
fun MonitorSavedCameraSettings(cameras: List<MonitorSavedCameraSummary>, emptyHelp: String) {
    MonitorSettingsRowCard(title = stringResource(R.string.mui_your_cameras)) {
        if (cameras.isEmpty()) {
            MonitorSettingsInlineRow(title = stringResource(R.string.mui_saved), help = emptyHelp, showTopDivider = false) {
                MonitorSettingsValueText(stringResource(R.string.mui_none))
            }
        } else {
            cameras.forEachIndexed { index, camera ->
                key(camera.id) {
                    MonitorSettingsInlineRow(title = camera.name, help = camera.detail, showTopDivider = index > 0) {
                        MonitorSettingsValueText(camera.network ?: stringResource(R.string.mui_saved))
                    }
                }
            }
        }
    }
}
