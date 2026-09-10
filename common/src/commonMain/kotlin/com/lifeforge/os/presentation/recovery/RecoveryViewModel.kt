package com.lifeforge.os.presentation.recovery

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.RecoveryEntry
import com.lifeforge.os.domain.repository.RecoveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class RecoveryState(
    val entries: List<RecoveryEntry> = emptyList(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val successfulDays: Int = 0,
    val relapseCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class RecoveryViewModel(
    private val recoveryRepository: RecoveryRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(RecoveryState())
    val state: StateFlow<RecoveryState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            recoveryRepository.observeEntries()
                .catch { e ->
                    _state.value = RecoveryState(isLoading = false, error = e.message)
                }
                .collect { entries ->
                    val current = runCatching { recoveryRepository.getCurrentStreak() }.getOrDefault(0)
                    val best = runCatching { recoveryRepository.getBestStreak() }.getOrDefault(0)
                    val successful = runCatching { recoveryRepository.getSuccessfulDays() }.getOrDefault(0)
                    val relapses = runCatching { recoveryRepository.getRelapses() }.getOrDefault(0)
                    _state.value = RecoveryState(
                        entries = entries.sortedByDescending { it.date },
                        currentStreak = current,
                        bestStreak = best,
                        successfulDays = successful,
                        relapseCount = relapses,
                        isLoading = false,
                        error = null,
                    )
                }
        }
    }

    fun addEntry(isRelapse: Boolean, trigger: String?, whatHappened: String?) {
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            recoveryRepository.saveEntry(
                RecoveryEntry(
                    id = newEntityId(),
                    date = now,
                    isRelapse = isRelapse,
                    trigger = trigger,
                    whatHappened = whatHappened,
                    streakAtTime = 0,
                )
            )
        }
    }
}