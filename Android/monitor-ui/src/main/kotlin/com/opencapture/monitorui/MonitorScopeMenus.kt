package com.opencapture.monitorui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opencapture.monitorui.settings.MonitorSettingsInlineRow
import com.opencapture.monitorui.settings.MonitorSettingsPercentSlider
import com.opencapture.monitorui.settings.MonitorSettingsChoices
import com.opencapture.monitorui.settings.MonitorSettingsSwitchInlineRow

@Composable
fun <Mode> MonitorScopeTraceMenu(
    modes: List<Mode>, mode: Mode, onMode: (Mode) -> Unit, title: (Mode) -> String,
    brightness: Int, onBrightness: (Int) -> Unit, guides: MonitorScopeGuides,
    onGuides: (MonitorScopeGuides) -> Unit, supportsCalibratedReferences: Boolean = true,
) {
    MonitorOptionGroup {
        MonitorSettingsInlineRow(stringResource(R.string.mui_mode), showTopDivider = false) {
            MonitorSettingsChoices(modes, mode, compact = false, accentSelection = true,
                title = title, onSelect = onMode)
        }
        MonitorSettingsInlineRow(stringResource(R.string.mui_brightness), help = stringResource(R.string.mui_brightness_help), stacked = true) {
            MonitorSettingsPercentSlider(brightness, 0..200, onBrightness)
        }
    }
    Spacer(Modifier.height(11.dp))
    if (!supportsCalibratedReferences) {
        Text(stringResource(R.string.mui_encoded_code_percentage), color = MonitorPalette.faint)
        return
    }
    Text(stringResource(R.string.mui_guide_lines), modifier = Modifier.padding(start = 12.dp, bottom = 5.dp),
        style = MonitorTypography.text(7.5f, FontWeight.Medium).copy(letterSpacing = 1.sp), color = MonitorPalette.faint)
    MonitorOptionGroup {
        MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_safe_clip), isOn = guides.clip, showTopDivider = false) {
            onGuides(guides.copy(clip = !guides.clip))
        }
        MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_safe_crush), isOn = guides.crush) {
            onGuides(guides.copy(crush = !guides.crush))
        }
        MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_middle_gray), isOn = guides.middle) {
            onGuides(guides.copy(middle = !guides.middle))
        }
    }
}

@Composable
fun MonitorVectorscopeMenu(zoom: MonitorVectorscopeZoom, onZoom: (MonitorVectorscopeZoom) -> Unit,
    brightness: Int, onBrightness: (Int) -> Unit) {
    MonitorSettingsInlineRow(stringResource(R.string.mui_trace_zoom), help = stringResource(R.string.mui_trace_zoom_help),
        showTopDivider = false, stacked = true) {
        MonitorSettingsChoices(MonitorVectorscopeZoom.entries, zoom, accentSelection = true,
            title = { it.label }, onSelect = onZoom)
    }
    MonitorSettingsInlineRow(stringResource(R.string.mui_brightness), help = stringResource(R.string.mui_chroma_brightness_help), stacked = true) {
        MonitorSettingsPercentSlider(brightness, 0..200, onBrightness)
    }
}
