package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeforge.os.core.preferences.ThemeMode
import com.lifeforge.os.core.preferences.Units
import com.lifeforge.os.presentation.localization.L10n
import com.lifeforge.os.presentation.settings.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, strings: L10n) {
    val themeMode by viewModel.themeMode.collectAsState()
    val units by viewModel.units.collectAsState()
    val language by viewModel.language.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        SettingsSection(title = strings.themeModeLabel) {
            RadioRow(
                label = strings.themeLight,
                selected = themeMode == ThemeMode.Light,
                onSelect = { viewModel.setThemeMode(ThemeMode.Light) },
            )
            RadioRow(
                label = strings.themeDark,
                selected = themeMode == ThemeMode.Dark,
                onSelect = { viewModel.setThemeMode(ThemeMode.Dark) },
            )
            RadioRow(
                label = strings.themeSystem,
                selected = themeMode == ThemeMode.System,
                onSelect = { viewModel.setThemeMode(ThemeMode.System) },
            )
        }

        SettingsSection(title = strings.unitsLabel) {
            RadioRow(
                label = strings.unitsMetric,
                selected = units == Units.Metric,
                onSelect = { viewModel.setUnits(Units.Metric) },
            )
            RadioRow(
                label = strings.unitsImperial,
                selected = units == Units.Imperial,
                onSelect = { viewModel.setUnits(Units.Imperial) },
            )
        }

        SettingsSection(title = strings.languageLabel) {
            RadioRow(
                label = strings.arabic,
                selected = language == "ar",
                onSelect = { viewModel.setLanguage("ar") },
            )
            RadioRow(
                label = strings.english,
                selected = language == "en",
                onSelect = { viewModel.setLanguage("en") },
            )
        }

        SettingsSection(title = strings.notificationsLabel) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = strings.notificationsLabel,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = viewModel::setNotificationsEnabled,
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(8.dp))
    Text(text = title, style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(4.dp))
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp).selectableGroup()) {
            content()
        }
    }
}

@Composable
private fun RadioRow(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}