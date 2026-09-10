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
import com.lifeforge.os.presentation.journal.JournalViewModel
import com.lifeforge.os.presentation.localization.L10n

@Composable
fun JournalScreen(viewModel: JournalViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var content by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(strings.noteContentHint) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.width(0.dp))
            Button(
                onClick = {
                    viewModel.addEntry(content)
                    content = ""
                },
                enabled = content.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text(strings.addRegularEntry)
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.entries.isEmpty() && !state.isLoading) item { Text(strings.noEntries) }
        items(state.entries, key = { it.id }) { entry ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatDate(entry.date),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { viewModel.deleteEntry(entry.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = strings.delete, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(entry.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

internal fun formatDate(millis: Long): String =
    kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        .let { "${it.dayOfMonth}/${it.monthNumber}/${it.year}" }