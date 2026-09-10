package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
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
import com.lifeforge.os.presentation.notes.NotesViewModel

@Composable
fun NotesScreen(viewModel: NotesViewModel, strings: L10n) {
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
                    viewModel.addNote(content)
                    content = ""
                },
                enabled = content.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text(strings.addNote)
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.notes.isEmpty() && !state.isLoading) item { Text(strings.noNotes) }
        items(state.notes, key = { it.id }) { note ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatDate(note.updatedAt),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { viewModel.togglePinned(note.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (note.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        IconButton(onClick = { viewModel.deleteNote(note.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = strings.delete, modifier = Modifier.size(18.dp))
                        }
                    }
                    note.plainText?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}