package com.lifeforge.os.presentation.home

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.startOfTodayMillis
import com.lifeforge.os.domain.repository.HabitRepository
import com.lifeforge.os.domain.repository.SettingsRepository
import com.lifeforge.os.domain.repository.TaskRepository
import com.lifeforge.os.domain.repository.UserProfileRepository
import com.lifeforge.os.domain.repository.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeState(
    val waterMl: Int = 0,
    val waterGoal: Int = 2500,
    val activeHabits: Int = 0,
    val openTasks: Int = 0,
    val userName: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class HomeViewModel(
    private val waterRepository: WaterRepository,
    private val habitRepository: HabitRepository,
    private val taskRepository: TaskRepository,
    settingsRepository: SettingsRepository,
    userProfileRepository: UserProfileRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            combine(
                waterRepository.observeDay(startOfTodayMillis()),
                habitRepository.observeActive(),
                taskRepository.observeOpen(),
                settingsRepository.waterGoal,
                userProfileRepository.profile,
            ) { logs, habits, tasks, goal, profile ->
                HomeState(
                    waterMl = logs.sumOf { it.amountMl },
                    waterGoal = goal,
                    activeHabits = habits.size,
                    openTasks = tasks.size,
                    userName = profile?.name,
                    isLoading = false,
                    error = null,
                )
            }.catch { e ->
                _state.value = HomeState(isLoading = false, error = e.message)
            }.collect { _state.value = it }
        }
    }

    fun addWater(amountMl: Int) {
        scopeProvider.ioScope.launch {
            waterRepository.addWater(
                amountMl = amountMl,
                date = startOfTodayMillis(),
                time = System.currentTimeMillis(),
            )
        }
    }
}