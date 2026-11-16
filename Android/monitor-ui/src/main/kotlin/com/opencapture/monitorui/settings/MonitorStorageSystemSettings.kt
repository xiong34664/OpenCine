package com.opencapture.monitorui.settings

import com.opencapture.monitorui.R

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import com.opencapture.monitorui.MonitorPalette

/** The host supplies cache scope, current IO state and actions. */
@Composable
public fun MonitorCacheSettings(
    cacheOriginals: Boolean? = null, sizeLabel: String, clearing: Boolean = false,
    cacheHelp: String = "", scopeHelp: String, clearHelp: String,
    onCacheOriginals: ((Boolean) -> Unit)? = null, clear: () -> Unit, feedback: () -> Unit = {},
) {
    MonitorSettingsRowCard {
        if (cacheOriginals != null && onCacheOriginals != null) {
            MonitorSettingsSwitchInlineRow(
                title = stringResource(R.string.mui_full_resolution_caching), help = cacheHelp,
                showTopDivider = false, isOn = cacheOriginals,
            ) { feedback(); onCacheOriginals(!cacheOriginals) }
        }
        MonitorSettingsInlineRow(stringResource(R.string.mui_local_media_cache), scopeHelp,
            showTopDivider = cacheOriginals != null && onCacheOriginals != null) {
            MonitorSettingsValueText(sizeLabel)
        }
        MonitorSettingsInlineRow(stringResource(R.string.mui_clear_cache), clearHelp) {
            if (clearing) MonitorSettingsValueText(stringResource(R.string.mui_clearing))
            else MonitorSettingsTextAction(stringResource(R.string.mui_clear), color = MonitorPalette.recording, onClick = clear)
        }
    }
}

/** App identity and protocol descriptions are supplied as content. */
@Composable
public fun MonitorAppInformationSettings(
    theme: String, protocolName: String, version: String,
    themeHelp: String, protocolHelp: String, versionHelp: String,
) {
    MonitorSettingsRowCard(title = stringResource(R.string.mui_app_information)) {
        MonitorSettingsInlineRow(stringResource(R.string.mui_theme), themeHelp, showTopDivider = false) { MonitorSettingsValueText(theme) }
        MonitorSettingsInlineRow(stringResource(R.string.mui_protocol_implementation), protocolHelp) { MonitorSettingsValueText(protocolName) }
        MonitorSettingsInlineRow(stringResource(R.string.mui_app_version), versionHelp) { MonitorSettingsValueText(version) }
    }
}

/** App-owned document and external navigation actions. */
@Composable
public fun MonitorProjectLegalSettings(
    sourceHelp: String, termsHelp: String,
    source: () -> Unit, privacy: () -> Unit, terms: () -> Unit,
    licenses: (() -> Unit)? = null, notice: (() -> Unit)? = null,
) {
    val openSourceDescription = stringResource(R.string.mui_open_source_code)
    val openPrivacyDescription = stringResource(R.string.mui_open_privacy_policy)
    val openTermsDescription = stringResource(R.string.mui_open_terms_of_use)
    val openLicensesDescription = stringResource(R.string.mui_open_licenses)
    val openNoticeDescription = stringResource(R.string.mui_open_notice)
    MonitorSettingsRowCard(title = stringResource(R.string.mui_project_legal)) {
        MonitorSettingsInlineRow(stringResource(R.string.mui_source_code), sourceHelp, showTopDivider = false) {
            MonitorSettingsActionPill(stringResource(R.string.mui_open), onClick = source,
                modifier = Modifier.semantics { contentDescription = openSourceDescription })
        }
        MonitorSettingsInlineRow(stringResource(R.string.mui_privacy), stringResource(R.string.mui_privacy_help)) {
            MonitorSettingsActionPill(stringResource(R.string.mui_open), onClick = privacy,
                modifier = Modifier.semantics { contentDescription = openPrivacyDescription })
        }
        MonitorSettingsInlineRow(stringResource(R.string.mui_terms), termsHelp) {
            MonitorSettingsActionPill(stringResource(R.string.mui_open), onClick = terms,
                modifier = Modifier.semantics { contentDescription = openTermsDescription })
        }
        licenses?.let { action ->
            MonitorSettingsInlineRow(stringResource(R.string.mui_licenses), stringResource(R.string.mui_licenses_help)) {
                MonitorSettingsActionPill(stringResource(R.string.mui_open), onClick = action,
                    modifier = Modifier.semantics { contentDescription = openLicensesDescription })
            }
        }
        notice?.let { action ->
            MonitorSettingsInlineRow(stringResource(R.string.mui_notice), stringResource(R.string.mui_notice_help)) {
                MonitorSettingsActionPill(stringResource(R.string.mui_open), onClick = action,
                    modifier = Modifier.semantics { contentDescription = openNoticeDescription })
            }
        }
    }
}
