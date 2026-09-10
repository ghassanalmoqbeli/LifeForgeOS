package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeforge.os.presentation.home.HomeViewModel
import com.lifeforge.os.presentation.localization.L10n
import com.lifeforge.os.presentation.navigation.FeatureRoute
import com.lifeforge.os.presentation.navigation.icon
import com.lifeforge.os.presentation.navigation.label

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    strings: L10n,
    onFeatureSelected: (FeatureRoute) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Text(
            text = strings.loading,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = state.userName?.let { "${strings.greeting} $it" } ?: strings.greeting,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(16.dp))

        // Water card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = strings.waterToday, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                val progress = state.waterMl.toFloat() / state.waterGoal.coerceAtLeast(1).toFloat()
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${state.waterMl} / ${state.waterGoal} ml",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.addWater(250) }) {
                    Text(strings.addWater)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        // Overview stats
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                label = strings.activeHabits,
                value = state.activeHabits.toString(),
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            StatCard(
                label = strings.openTasks,
                value = state.openTasks.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(strings.featuresLabel, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        FeatureGrid(strings = strings, onFeatureSelected = onFeatureSelected)
    }
}

@Composable
private fun FeatureGrid(
    strings: L10n,
    onFeatureSelected: (FeatureRoute) -> Unit,
) {
    val routes = com.lifeforge.os.presentation.navigation.allFeatureRoutes
    routes.chunked(3).forEach { rowRoutes ->
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            rowRoutes.forEach { route ->
                FeatureTile(
                    route = route,
                    strings = strings,
                    onClick = { onFeatureSelected(route) },
                    modifier = Modifier.weight(1f),
                )
                if (route != rowRoutes.last()) Spacer(Modifier.width(8.dp))
            }
            if (rowRoutes.size < 3) {
                repeat(3 - rowRoutes.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun FeatureTile(
    route: FeatureRoute,
    strings: L10n,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = route.icon(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = route.label(strings),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
    }
}