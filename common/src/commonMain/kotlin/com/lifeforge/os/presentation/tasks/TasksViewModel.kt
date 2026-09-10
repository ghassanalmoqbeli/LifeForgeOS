package com.lifeforge.os.presentation.tasks

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.Task
import com.lifeforge.os.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class TasksState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            taskRepository.observeOpen()
                .catch { e ->
                    _state.value = TasksState(isLoading = false, error = e.message)
                }
                .collect { tasks ->
                    _state.value = TasksState(tasks = tasks, isLoading = false, error = null)
                }
        }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        val trimmed = title.trim()
        if (trimmed.length > 200) return
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            taskRepository.save(
                Task(
                    id = newEntityId(),
                    title = trimmed,
                    createdAt = now,
                    updatedAt = now,
                )
            )
        }
    }

    fun toggleTask(taskId: String) {
        scopeProvider.ioScope.launch { taskRepository.toggleCompleted(taskId) }
    }

    fun deleteTask(taskId: String) {
        scopeProvider.ioScope.launch { taskRepository.delete(taskId) }
    }
}