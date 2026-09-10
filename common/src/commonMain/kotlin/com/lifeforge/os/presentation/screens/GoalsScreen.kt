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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.lifeforge.os.presentation.goals.GoalsViewModel
import com.lifeforge.os.presentation.localization.L10n

@Composable
fun GoalsScreen(viewModel: GoalsViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(strings.title) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = target,
                onValueChange = { target = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text(strings.goalTarget) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.addGoal(title, target.toDoubleOrNull() ?: 0.0)
                    title = ""
                    target = ""
                },
                enabled = title.isNotBlank() && (target.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(strings.addGoal)
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.goals.isEmpty() && !state.isLoading) item { Text(strings.noGoals) }
        items(state.goals, key = { it.id }) { goal ->
            val progress = (goal.currentValue / goal.targetValue).coerceIn(0.0, 1.0).toFloat()
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(goal.title, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} (${(progress * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).padding(top = 4.dp),
                            )
                        }
                        Button(onClick = { viewModel.logProgress(goal.id) }, modifier = Modifier.padding(start = 8.dp)) {
                            Text("+10%")
                        }
                        IconButton(onClick = { viewModel.deleteGoal(goal.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = strings.delete)
                        }
                    }
                }
            }
        }
    }
}