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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
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
import com.lifeforge.os.presentation.gym.GymViewModel
import com.lifeforge.os.presentation.localization.L10n

@Composable
fun GymScreen(viewModel: GymViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(
                text = "${strings.recentSessionsLabel}: ${state.recentSessions.count { it.isCompleted }}",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.search(it)
                },
                label = { Text(strings.search) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        items(state.searchResults, key = { it.id }) { exercise ->
            ExerciseRow(exercise.name, favorite = exercise.isFavorite, onToggleFavorite = { viewModel.toggleFavorite(exercise.id) })
        }
        item {
            Spacer(Modifier.height(4.dp))
            Text(strings.favorites, style = MaterialTheme.typography.titleMedium)
        }
        items(state.favorites, key = { it.id }) { exercise ->
            ExerciseRow(exercise.name, favorite = true, onToggleFavorite = { viewModel.toggleFavorite(exercise.id) })
        }
    }
}

@Composable
private fun ExerciseRow(name: String, favorite: Boolean, onToggleFavorite: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (favorite) Icons.Filled.Star else Icons.Filled.Check,
                    contentDescription = null,
                )
            }
        }
    }
}