package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.lifeforge.os.presentation.localization.L10n
import com.lifeforge.os.presentation.nutrition.NutritionViewModel

@Composable
fun NutritionScreen(viewModel: NutritionViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "${strings.calories}: ${state.calorieTotal.toInt()} / ${state.caloriesGoal}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    LinearProgressIndicator(
                        progress = { (state.calorieTotal / state.caloriesGoal).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row {
                        MacroPill("${strings.protein} ${state.proteinTotal.toInt()}g", Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        MacroPill("${strings.carbs} ${state.carbsTotal.toInt()}g", Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        MacroPill("${strings.fat} ${state.fatTotal.toInt()}g", Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "${strings.waterToday}: ${state.waterToday} / ${state.waterGoal} ml",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    LinearProgressIndicator(
                        progress = { (state.waterToday.toFloat() / state.waterGoal).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.addWater() }, enabled = state.waterToday < state.waterGoal) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(strings.quickAddWater)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.searchFoods(it)
                },
                label = { Text(strings.search) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        items(state.searchResults, key = { it.id }) { food ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text(food.name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Text("${food.facts.calories.toInt()} kcal", style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { viewModel.addFoodToMeal(food) }) {
                    Icon(Icons.Filled.Add, contentDescription = strings.add)
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Text(strings.today, style = MaterialTheme.typography.titleMedium)
        }

        items(state.meals, key = { it.id }) { meal ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = meal.type.name,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f),
                        )
                        Text("${meal.total.calories.toInt()} kcal", style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { viewModel.deleteMeal(meal.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = strings.delete, modifier = Modifier.size(18.dp))
                        }
                    }
                    meal.items.forEach { item ->
                        Text(
                            text = "• ${item.foodName} (${item.facts.calories.toInt()} kcal)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroPill(text: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(text, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(8.dp))
    }
}