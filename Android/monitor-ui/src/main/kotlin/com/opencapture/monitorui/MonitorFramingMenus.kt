package com.opencapture.monitorui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opencapture.monitorui.settings.MonitorSettingsChoices
import com.opencapture.monitorui.settings.MonitorSettingsInlineRow
import com.opencapture.monitorui.settings.MonitorSettingsSwitchInlineRow
import com.opencapture.opencine.media.MonitorNDFilterNotation
import com.opencapture.opencine.media.MonitorDesqueezePreset
import com.opencapture.opencine.media.MonitorDesqueezeDirection
import com.opencapture.opencine.media.MonitorAnamorphicDesqueeze

@Composable
fun MonitorAssistHelp(text: String) { Text(text, color = MonitorPalette.muted, fontSize = 13.sp) }

@Composable
fun MonitorAudioMenu(orientation: MonitorAudioOrientation, onOrientation: (MonitorAudioOrientation) -> Unit,
    showDB: Boolean, onShowDB: (Boolean) -> Unit, help: String) {
    MonitorSettingsInlineRow(stringResource(R.string.mui_orientation), help = help, showTopDivider = false, stacked = true) {
        MonitorSettingsChoices(MonitorAudioOrientation.entries, orientation, accentSelection = true,
            title = { it.label }, onSelect = onOrientation)
    }
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_show_db_values), isOn = showDB, stacked = false) { onShowDB(!showDB) }
}

@Composable
fun MonitorNDMenu(notation: MonitorNDFilterNotation, onNotation: (MonitorNDFilterNotation) -> Unit,
    title: String, notationHelp: String, help: String) {
    MonitorSettingsInlineRow(title, help = notationHelp, showTopDivider = false, stacked = true) {
        MonitorSettingsChoices(MonitorNDFilterNotation.entries, notation, accentSelection = true,
            title = { it.editorLabel }, onSelect = onNotation)
    }
    MonitorAssistHelp(help)
}

@Composable
fun MonitorMirrorMenu(horizontal: Boolean, onHorizontal: (Boolean) -> Unit,
    vertical: Boolean, onVertical: (Boolean) -> Unit, explanation: String,
    horizontalHelp: String, verticalHelp: String) {
    MonitorAssistHelp(explanation)
    Spacer(Modifier.height(8.dp))
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_horizontal), help = horizontalHelp, isOn = horizontal,
        showTopDivider = false, stacked = false) { onHorizontal(!horizontal) }
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_vertical), help = verticalHelp, isOn = vertical,
        stacked = false) { onVertical(!vertical) }
}

@Composable
fun MonitorGridMenu(thirds: Boolean, onThirds: (Boolean) -> Unit, phi: Boolean,
    onPhi: (Boolean) -> Unit, diagonal: Boolean, onDiagonal: (Boolean) -> Unit) {
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_thirds), isOn = thirds, showTopDivider = false, stacked = false) { onThirds(!thirds) }
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_phi_grid), isOn = phi, stacked = false) { onPhi(!phi) }
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_diagonal), isOn = diagonal, stacked = false) { onDiagonal(!diagonal) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonitorGuidesMenu(family: MonitorGuideFamily, selected: Set<MonitorGuideAspect>, mask: Boolean,
    onFamily: (MonitorGuideFamily) -> Unit, onToggle: (MonitorGuideAspect) -> Unit,
    onMask: (Boolean) -> Unit, ratioFeedback: () -> Unit = {},
    choiceBackground: Color = Color(94 / 255f, 98 / 255f, 98 / 255f, .18f)) {
    val shownSelection = selected.toSet()
    Row(Modifier.fillMaxWidth().monitorTabStrip()) {
        MonitorGuideFamily.entries.forEachIndexed { index, option ->
            val active = family == option
            MonitorTab(active, { onFamily(option) }, Modifier.weight(1f), separator = index > 0,
                accessibilityLabel = option.label) {
                Text(option.label, color = if (active) MonitorPalette.accent else MonitorPalette.muted,
                    style = MonitorTypography.text(13f, FontWeight.SemiBold), maxLines = 1)
            }
        }
    }
    Spacer(Modifier.height(10.dp))
    val shape = RoundedCornerShape(12.dp)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MonitorGuideAspect.ratios(family).forEach { aspect ->
            val active = aspect in shownSelection
            Text(aspect.label, color = if (active) Color(0xFF08191F) else MonitorPalette.text,
                style = MonitorTypography.text(14f),
                modifier = Modifier.clip(shape).background(if (active) MonitorPalette.accent else choiceBackground)
                    .border(1.dp, if (active) MonitorPalette.accent.copy(alpha = .16f) else MonitorPalette.border, shape)
                    .monitorChromeClickable(onClickFeedback = ratioFeedback) { onToggle(aspect) }
                    .padding(horizontal = 10.dp, vertical = 12.dp))
        }
    }
    Spacer(Modifier.height(10.dp))
    MonitorSettingsSwitchInlineRow(stringResource(R.string.mui_mask_outside_frame), isOn = mask, showTopDivider = false,
        stacked = false) { onMask(!mask) }
}

@Composable
fun MonitorDesqueezeMenu(preset: MonitorDesqueezePreset, custom: Boolean, factor: Double,
    direction: MonitorDesqueezeDirection, help: String, onSelect: (MonitorDesqueezePreset?) -> Unit,
    onFactor: (Double) -> Unit, onDirection: (MonitorDesqueezeDirection) -> Unit) {
    val customLabel = stringResource(R.string.mui_custom)
    MonitorSettingsInlineRow(stringResource(R.string.mui_ratio), help = help, showTopDivider = false, stacked = true) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val selected = if (custom) null else preset
            (MonitorDesqueezePreset.entries.map { it as MonitorDesqueezePreset? } + null).chunked(4).forEach { row ->
                MonitorSettingsChoices(row, selected, accentSelection = true,
                    title = { it?.label ?: customLabel }, onSelect = onSelect)
            }
        }
    }
    if (custom) {
        val desqueezeDescription = stringResource(R.string.mui_anamorphic_desqueeze_ratio)
        MonitorSettingsInlineRow(stringResource(R.string.mui_custom_ratio), help = stringResource(R.string.mui_custom_ratio_help), stacked = true) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Slider(value = factor.toFloat(), onValueChange = { onFactor(it.toDouble()) },
                    valueRange = 1f..2f, steps = 99, modifier = Modifier.weight(1f).semantics {
                        contentDescription = desqueezeDescription
                        stateDescription = MonitorAnamorphicDesqueeze.label(factor)
                    })
                Text(MonitorAnamorphicDesqueeze.label(factor), style = MonitorTypography.text(12f, FontWeight.Medium),
                    color = MonitorPalette.text)
            }
        }
    }
    MonitorSettingsInlineRow(stringResource(R.string.mui_direction), stacked = true) {
        MonitorSettingsChoices(MonitorDesqueezeDirection.entries, direction, accentSelection = true,
            title = { it.label }, onSelect = onDirection)
    }
}
