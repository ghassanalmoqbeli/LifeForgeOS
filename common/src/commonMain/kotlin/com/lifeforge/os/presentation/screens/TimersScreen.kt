package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeforge.os.presentation.localization.L10n
import com.lifeforge.os.presentation.timers.TimersViewModel

@Composable
fun TimersScreen(viewModel: TimersViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(strings.taskTitleHint) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = seconds,
                    onValueChange = { seconds = it.filter(Char::isDigit) },
                    label = { Text(strings.seconds) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addPreset(name, seconds.toIntOrNull() ?: 0)
                        name = ""
                        seconds = ""
                    },
                    enabled = name.isNotBlank() && (seconds.toIntOrNull() ?: 0) > 0,
                ) {
                    Text(strings.addTimer)
                }
            }
        }

        if (state.isLoading) item { Text(strings.loading) }
        if (state.presets.isEmpty() && !state.isLoading) item { Text(strings.noTimers) }

        items(state.presets, key = { it.id }) { preset ->
            val running = state.runningPresetId == preset.id
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(preset.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = if (running) formatDuration(state.remainingSeconds) else formatDuration(preset.totalSeconds),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    if (running) {
                        Button(onClick = { viewModel.stop() }) { Text(strings.stop) }
                    } else {
                        IconButton(onClick = { viewModel.startPreset(preset.id) }) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = strings.start)
                        }
                    }
                    IconButton(onClick = { viewModel.deletePreset(preset.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = strings.delete)
                    }
                }
            }
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}