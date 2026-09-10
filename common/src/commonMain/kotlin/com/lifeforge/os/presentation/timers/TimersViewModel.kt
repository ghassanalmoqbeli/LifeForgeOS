package com.lifeforge.os.presentation.timers

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.TimerPhase
import com.lifeforge.os.domain.model.TimerPreset
import com.lifeforge.os.domain.repository.TimerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TimersState(
    val presets: List<TimerPreset> = emptyList(),
    val runningPresetId: String? = null,
    val remainingSeconds: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class TimersViewModel(
    private val timerRepository: TimerRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(TimersState())
    val state: StateFlow<TimersState> = _state.asStateFlow()
    private var tickJob: Job? = null

    init {
        scopeProvider.defaultScope.launch {
            timerRepository.observePresets()
                .catch { e ->
                    _state.value = TimersState(isLoading = false, error = e.message)
                }
                .collect { presets ->
                    val current = _state.value
                    _state.value = TimersState(
                        presets = presets,
                        runningPresetId = current.runningPresetId,
                        remainingSeconds = current.remainingSeconds,
                        isLoading = false,
                        error = null,
                    )
                }
        }
    }

    fun addPreset(name: String, totalSeconds: Int) {
        if (name.isBlank() || totalSeconds <= 0) return
        scopeProvider.ioScope.launch {
            timerRepository.save(
                TimerPreset(
                    id = newEntityId(),
                    name = name.trim(),
                    totalSeconds = totalSeconds,
                    phases = listOf(TimerPhase(name = name.trim(), durationSeconds = totalSeconds)),
                )
            )
        }
    }

    fun deletePreset(id: String) {
        if (_state.value.runningPresetId == id) stop()
        scopeProvider.ioScope.launch { timerRepository.delete(id) }
    }

    fun startPreset(id: String) {
        val preset = _state.value.presets.firstOrNull { it.id == id } ?: return
        stop()
        _state.update { it.copy(runningPresetId = id, remainingSeconds = preset.totalSeconds) }
        tickJob?.cancel()
        tickJob = scopeProvider.defaultScope.launch {
            while (isActive) {
                delay(1000)
                _state.update { s ->
                    val remaining = s.remainingSeconds - 1
                    if (remaining <= 0) {
                        s.copy(remainingSeconds = 0)
                    } else {
                        s.copy(remainingSeconds = remaining)
                    }
                }
            }
        }
    }

    fun stop() {
        tickJob?.cancel()
        tickJob = null
        _state.update { it.copy(runningPresetId = null, remainingSeconds = 0) }
    }
}