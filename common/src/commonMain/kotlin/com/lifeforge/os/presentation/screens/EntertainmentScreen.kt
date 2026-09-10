package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeforge.os.domain.model.MediaItem
import com.lifeforge.os.domain.model.MediaStatus
import com.lifeforge.os.presentation.entertainment.EntertainmentViewModel
import com.lifeforge.os.presentation.localization.L10n

@Composable
fun EntertainmentScreen(viewModel: EntertainmentViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var title by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(strings.mediaTitle) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.addItem(title)
                    title = ""
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(strings.addItem)
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.items.isEmpty() && !state.isLoading) item { Text(strings.noMedia) }
        items(state.items, key = { it.id }) {
            MediaRow(it, strings, onCycleStatus = { viewModel.cycleStatus(it.id) }, onDelete = { viewModel.deleteItem(it.id) })
        }
    }
}

@Composable
private fun MediaRow(
    item: MediaItem,
    strings: L10n,
    onCycleStatus: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${item.type.name} • ${strings.statusLabel}: ${item.status.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            Button(onClick = onCycleStatus, modifier = Modifier.padding(start = 8.dp)) {
                Text(when (item.status) {
                    MediaStatus.WantToWatch -> "▶"
                    MediaStatus.Watching -> "✓"
                    MediaStatus.Completed -> "↺"
                    MediaStatus.Paused -> "▶"
                    MediaStatus.Dropped -> "▶"
                })
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = strings.delete)
            }
        }
    }
}