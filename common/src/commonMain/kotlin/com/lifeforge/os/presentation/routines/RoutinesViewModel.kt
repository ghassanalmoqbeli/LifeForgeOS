package com.lifeforge.os.presentation.routines

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.Routine
import com.lifeforge.os.domain.model.RoutineItem
import com.lifeforge.os.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class RoutinesState(
    val routines: List<Routine> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class RoutinesViewModel(
    private val routineRepository: RoutineRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(RoutinesState())
    val state: StateFlow<RoutinesState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            routineRepository.observeAll()
                .catch { e ->
                    _state.value = RoutinesState(isLoading = false, error = e.message)
                }
                .collect { routines ->
                    _state.value = RoutinesState(routines = routines, isLoading = false, error = null)
                }
        }
    }

    fun addRoutine(name: String) {
        if (name.isBlank()) return
        scopeProvider.ioScope.launch {
            routineRepository.save(
                Routine(id = newEntityId(), name = name.trim(), items = emptyList())
            )
        }
    }

    fun addItem(routineId: String, itemName: String) {
        if (itemName.isBlank()) return
        scopeProvider.ioScope.launch {
            val routine = routineRepository.getById(routineId) ?: return@launch
            val items = routine.items + RoutineItem(
                id = newEntityId(),
                routineId = routineId,
                name = itemName.trim(),
                time = System.currentTimeMillis(),
                order = routine.items.size,
            )
            routineRepository.save(routine.copy(items = items))
        }
    }

    fun toggleItem(itemId: String, completed: Boolean) {
        scopeProvider.ioScope.launch {
            routineRepository.completeItem(itemId, completed)
        }
    }

    fun deleteRoutine(id: String) {
        scopeProvider.ioScope.launch { routineRepository.delete(id) }
    }
}