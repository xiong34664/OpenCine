package com.opencapture.monitorui.settings

import com.opencapture.monitorui.R

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource

/** Optional queued report values from an app-owned reporting workflow. */
public data class MonitorReportStatus(val label: String, val detail: String, val id: String? = null)

/** A focused action from the host's real support workflow. */
public data class MonitorSupportAction(
    val id: String, val title: String, val help: String? = null, val actionTitle: String,
    val accessibilityLabel: String, val enabled: Boolean = true, val action: () -> Unit,
)

@Composable
public fun MonitorSupportSettings(
    automaticReports: Boolean? = null, available: Boolean = false, automaticHelp: String = "", unavailableHelp: String = "",
    onAutomaticReports: ((Boolean) -> Unit)? = null, report: () -> Unit, privacy: (() -> Unit)? = null,
    status: MonitorReportStatus? = null, discard: (() -> Unit)? = null, feedback: () -> Unit = {},
    reportTitle: String = stringResource(R.string.mui_report_a_problem),
    reportHelp: String = stringResource(R.string.mui_report_help),
    reportActionTitle: String = stringResource(R.string.mui_open), actions: List<MonitorSupportAction> = emptyList(),
) {
    MonitorSettingsRowCard(title = stringResource(R.string.mui_help_feedback)) {
        MonitorSettingsInlineRow(reportTitle, reportHelp, showTopDivider = false) {
            MonitorSettingsActionPill(reportActionTitle, onClick = report,
                modifier = Modifier.testTag("support.report.open").semantics { contentDescription = reportTitle })
        }
        actions.forEach { action ->
            MonitorSettingsInlineRow(action.title, action.help) {
                MonitorSettingsActionPill(action.actionTitle, enabled = action.enabled, onClick = action.action,
                    modifier = Modifier.testTag(action.id).semantics { contentDescription = action.accessibilityLabel })
            }
        }
        if (status != null) {
            MonitorSettingsInlineRow(stringResource(R.string.mui_report_status), status.detail) { MonitorSettingsValueText(status.label) }
            status.id?.let { id ->
                MonitorSettingsInlineRow(stringResource(R.string.mui_report_id), stringResource(R.string.mui_report_id_help)) { MonitorSettingsValueText(id) }
            }
            if (discard != null) {
                MonitorSettingsInlineRow(stringResource(R.string.mui_queued_report), stringResource(R.string.mui_queued_report_help)) {
                    MonitorSettingsActionPill(stringResource(R.string.mui_discard), onClick = discard)
                }
            }
        }
        if (automaticReports != null) {
            if (available && onAutomaticReports != null) {
                MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_automatic_error_reports), automaticReports, automaticHelp) {
                    feedback(); onAutomaticReports(!automaticReports)
                }
            } else {
                MonitorSettingsInlineRow(stringResource(R.string.mui_automatic_error_reports), unavailableHelp) { MonitorSettingsValueText(stringResource(R.string.mui_off)) }
            }
        }
        privacy?.let { action ->
            MonitorSettingsInlineRow(stringResource(R.string.mui_reporting_privacy), stringResource(R.string.mui_reporting_privacy_help)) {
                MonitorSettingsActionPill(stringResource(R.string.mui_read_action), onClick = action)
            }
        }
    }
}

@Composable
public fun MonitorDiagnosticSettings(
    expanded: Boolean, onExpanded: (Boolean) -> Unit, deleteHelp: String,
    save: () -> Unit, delete: () -> Unit,
) {
    MonitorSettingsGroupCard(title = stringResource(R.string.mui_diagnostic_options), caption = stringResource(R.string.mui_diagnostic_options_caption),
        expanded = expanded, onExpandToggle = { onExpanded(!expanded) }) {
        MonitorSettingsInlineRow(stringResource(R.string.mui_save_diagnostic_report), stringResource(R.string.mui_save_diagnostic_report_help),
            showTopDivider = false) { MonitorSettingsActionPill(stringResource(R.string.mui_save), onClick = save) }
        MonitorSettingsInlineRow(stringResource(R.string.mui_delete_saved_feed_reports), deleteHelp) {
            MonitorSettingsActionPill(stringResource(R.string.mui_delete), onClick = delete)
        }
    }
}
