package com.lifeforge.os.presentation.gym

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.domain.model.Exercise
import com.lifeforge.os.domain.model.WorkoutSession
import com.lifeforge.os.domain.repository.ExerciseRepository
import com.lifeforge.os.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class GymState(
    val favorites: List<Exercise> = emptyList(),
    val searchResults: List<Exercise> = emptyList(),
    val recentSessions: List<WorkoutSession> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class GymViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(GymState())
    val state: StateFlow<GymState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            combine(
                exerciseRepository.observeFavorites(),
                workoutRepository.observeRecent(10),
            ) { favorites, recent ->
                GymState(
                    favorites = favorites,
                    recentSessions = recent,
                    isLoading = false,
                    error = null,
                )
            }.catch { e ->
                _state.value = GymState(isLoading = false, error = e.message)
            }.collect { _state.value = it }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _state.value = _state.value.copy(searchResults = emptyList())
            return
        }
        scopeProvider.ioScope.launch {
            _state.value = _state.value.copy(searchResults = exerciseRepository.search(query))
        }
    }

    fun toggleFavorite(id: String) {
        scopeProvider.ioScope.launch { exerciseRepository.toggleFavorite(id) }
    }
}