package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeforge.os.presentation.localization.L10n
import com.lifeforge.os.presentation.recovery.RecoveryViewModel

@Composable
fun RecoveryScreen(viewModel: RecoveryViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var trigger by remember { mutableStateOf("") }
    var happened by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "${strings.streakLabel}: ${state.currentStreak} ${strings.dayLabel}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Best: ${state.bestStreak} • ${strings.completed}: ${state.successfulDays} • ${strings.relapse}: ${state.relapseCount}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = trigger,
                onValueChange = { trigger = it },
                label = { Text(strings.relapseTrigger) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = happened,
                onValueChange = { happened = it },
                label = { Text(strings.whatHappened) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Row {
                Button(
                    onClick = {
                        viewModel.addEntry(isRelapse = true, trigger = trigger.trim().ifBlank { null }, whatHappened = happened.trim().ifBlank { null })
                        trigger = ""
                        happened = ""
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(strings.addRelapseEntry)
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addEntry(isRelapse = false, trigger = null, whatHappened = happened.trim().ifBlank { null })
                        happened = ""
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(strings.addRegularEntry)
                }
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.entries.isEmpty() && !state.isLoading) item { Text(strings.noEntries) }
        items(state.entries, key = { it.id }) { entry ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = buildString {
                            append(formatDate(entry.date))
                            append(if (entry.isRelapse) " • ${strings.relapse}" else " • ✓")
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                    entry.trigger?.let { Text("${strings.relapseTrigger}: $it", style = MaterialTheme.typography.bodyMedium) }
                    entry.whatHappened?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}