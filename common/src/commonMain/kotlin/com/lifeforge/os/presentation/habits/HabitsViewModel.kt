package com.lifeforge.os.presentation.habits

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.core.utils.startOfTodayMillis
import com.lifeforge.os.domain.model.Habit
import com.lifeforge.os.domain.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HabitsState(
    val habits: List<Habit> = emptyList(),
    val completedToday: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class HabitsViewModel(
    private val habitRepository: HabitRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(HabitsState())
    val state: StateFlow<HabitsState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            combine(
                habitRepository.observeActive(),
                habitRepository.observeDayLog(startOfTodayMillis()),
            ) { habits, dayLog ->
                HabitsState(
                    habits = habits,
                    completedToday = dayLog.filterValues { it }.keys,
                    isLoading = false,
                    error = null,
                )
            }.catch { e ->
                _state.value = HabitsState(isLoading = false, error = e.message)
            }.collect { _state.value = it }
        }
    }

    fun addHabit(name: String) {
        if (name.isBlank()) return
        val trimmed = name.trim()
        if (trimmed.length > 100) return
        scopeProvider.ioScope.launch {
            habitRepository.save(
                Habit(
                    id = newEntityId(),
                    name = trimmed,
                    startDate = System.currentTimeMillis(),
                )
            )
        }
    }

    fun toggleHabit(habitId: String) {
        scopeProvider.ioScope.launch {
            habitRepository.toggleCompletion(habitId, startOfTodayMillis())
        }
    }

    fun deleteHabit(habitId: String) {
        scopeProvider.ioScope.launch { habitRepository.delete(habitId) }
    }
}