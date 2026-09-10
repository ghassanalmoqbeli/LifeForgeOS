package com.lifeforge.os.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lifeforge.os.presentation.localization.L10n
import com.lifeforge.os.presentation.onboarding.OnboardingViewModel

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(text = strings.onboarding, style = MaterialTheme.typography.headlineMedium)
        Text(text = strings.profileTitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = viewModel::setName,
            label = { Text(strings.nameLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.waterGoal.toString(),
            onValueChange = { viewModel.setWaterGoal(it.toIntOrNull() ?: 0) },
            label = { Text(strings.waterGoalLabel) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.caloriesGoal.toString(),
            onValueChange = { viewModel.setCaloriesGoal(it.toIntOrNull() ?: 0) },
            label = { Text(strings.caloriesGoalLabel) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        Text(text = strings.languageLabel, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Row {
            OutlinedButton(onClick = { viewModel.setLanguage("ar") }) {
                Text(strings.arabic)
            }
            Spacer(Modifier.width(12.dp))
            OutlinedButton(onClick = { viewModel.setLanguage("en") }) {
                Text(strings.english)
            }
        }

        if (state.error != null && state.error == "name_required") {
            Spacer(Modifier.height(8.dp))
            Text(
                text = strings.enteredNameRequired,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = viewModel::complete,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.isSaving) strings.loading else strings.continueLabel)
        }
    }
}