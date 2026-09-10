package com.lifeforge.os.presentation.goals

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.Goal
import com.lifeforge.os.domain.model.GoalCategory
import com.lifeforge.os.domain.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class GoalsState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class GoalsViewModel(
    private val goalRepository: GoalRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(GoalsState())
    val state: StateFlow<GoalsState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            goalRepository.observeActive()
                .catch { e ->
                    _state.value = GoalsState(isLoading = false, error = e.message)
                }
                .collect { goals ->
                    _state.value = GoalsState(goals = goals, isLoading = false, error = null)
                }
        }
    }

    fun addGoal(title: String, target: Double) {
        if (title.isBlank() || target <= 0) return
        scopeProvider.ioScope.launch {
            goalRepository.save(
                Goal(
                    id = newEntityId(),
                    title = title.trim(),
                    category = GoalCategory.Personal,
                    targetValue = target,
                )
            )
        }
    }

    fun logProgress(id: String) {
        scopeProvider.ioScope.launch {
            val goal = goalRepository.getById(id) ?: return@launch
            val step = (goal.targetValue * 0.1).coerceAtLeast(1.0)
            val current = goal.currentValue + step
            val progress = (current / goal.targetValue).coerceIn(0.0, 1.0)
            goalRepository.save(goal.copy(currentValue = current, progress = progress))
        }
    }

    fun deleteGoal(id: String) {
        scopeProvider.ioScope.launch { goalRepository.delete(id) }
    }
}