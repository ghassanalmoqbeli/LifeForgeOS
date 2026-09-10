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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeforge.os.domain.model.CourseStatus
import com.lifeforge.os.presentation.courses.CoursesViewModel
import com.lifeforge.os.presentation.localization.L10n

@Composable
fun CoursesScreen(viewModel: CoursesViewModel, strings: L10n) {
    val state by viewModel.state.collectAsState()
    var title by remember { mutableStateOf("") }
    var provider by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(strings.courseTitle) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                label = { Text(strings.courseProvider) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.addCourse(title, provider)
                    title = ""
                    provider = ""
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(strings.addCourse)
            }
        }
        if (state.isLoading) item { Text(strings.loading) }
        if (state.courses.isEmpty() && !state.isLoading) item { Text(strings.noCourses) }
        items(state.courses, key = { it.id }) { course ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row {
                        Column(Modifier.weight(1f)) {
                            Text(course.title, style = MaterialTheme.typography.bodyLarge)
                            course.provider?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                            Text(
                                text = "${(course.progressPercent * 100).toInt()}% • ${course.status.name}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            LinearProgressIndicator(
                                progress = { course.progressPercent.toFloat() },
                                modifier = Modifier.fillMaxWidth().height(6.dp).padding(top = 6.dp),
                            )
                        }
                        Button(
                            onClick = { viewModel.bumpProgress(course.id) },
                            enabled = course.status != CourseStatus.Completed,
                            modifier = Modifier.padding(start = 8.dp),
                        ) {
                            Text("+10%")
                        }
                        IconButton(onClick = { viewModel.deleteCourse(course.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = strings.delete)
                        }
                    }
                }
            }
        }
    }
}