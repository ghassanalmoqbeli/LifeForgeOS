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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import com.lifeforge.os.domain.model.Routine
import com.lifeforge.os.presentation.localization.L10n
import com.lifeforge.os.presentation.routines.RoutinesViewModel

@Composable
fun RoutinesScreen(viewModel: RoutinesViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var routineName by remember { mutableStateOf("") }
    var pendingItemNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { routineName = it },
                    label = { Text(strings.addRoutine) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addRoutine(routineName)
                        routineName = ""
                    },
                    enabled = routineName.isNotBlank(),
                ) {
                    Text(strings.add)
                }
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.routines.isEmpty() && !state.isLoading) item { Text(strings.noRoutines) }
        items(state.routines, key = { it.id }) { routine ->
            RoutineCard(
                routine = routine,
                pendingItemNames = pendingItemNames[routine.id].orEmpty(),
                onPendingItemChange = { pendingItemNames = pendingItemNames + (routine.id to it) },
                onAddItem = { itemName ->
                    viewModel.addItem(routine.id, itemName)
                    pendingItemNames = pendingItemNames - routine.id
                },
                onToggleItem = { id, completed -> viewModel.toggleItem(id, completed) },
                onDelete = { viewModel.deleteRoutine(routine.id) },
                strings = strings,
            )
        }
    }
}

@Composable
private fun RoutineCard(
    routine: Routine,
    pendingItemNames: String,
    onPendingItemChange: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onToggleItem: (String, Boolean) -> Unit,
    onDelete: () -> Unit,
    strings: L10n,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(routine.name, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = strings.delete, modifier = Modifier.size(20.dp))
                }
            }
            if (routine.items.isEmpty()) {
                Text(strings.noItems, style = MaterialTheme.typography.bodyMedium)
            } else {
                routine.items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { onToggleItem(item.id, it) },
                        )
                        Text(item.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = pendingItemNames,
                    onValueChange = onPendingItemChange,
                    label = { Text(strings.title) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { onAddItem(pendingItemNames) }, enabled = pendingItemNames.isNotBlank()) {
                    Icon(Icons.Filled.Add, contentDescription = strings.add)
                }
            }
        }
    }
}