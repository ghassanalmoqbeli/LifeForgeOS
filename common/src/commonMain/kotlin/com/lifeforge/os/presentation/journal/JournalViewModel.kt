package com.lifeforge.os.presentation.journal

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.JournalEntry
import com.lifeforge.os.domain.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class JournalState(
    val entries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class JournalViewModel(
    private val journalRepository: JournalRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(JournalState())
    val state: StateFlow<JournalState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            journalRepository.observeAll()
                .catch { e ->
                    _state.value = JournalState(isLoading = false, error = e.message)
                }
                .collect { entries ->
                    _state.value = JournalState(entries = entries.sortedByDescending { it.date }, isLoading = false, error = null)
                }
        }
    }

    fun addEntry(content: String) {
        if (content.isBlank()) return
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            journalRepository.save(
                JournalEntry(
                    id = newEntityId(),
                    date = now,
                    content = content.trim(),
                    mood = null,
                    createdAt = now,
                    updatedAt = now,
                )
            )
        }
    }

    fun deleteEntry(id: String) {
        scopeProvider.ioScope.launch { journalRepository.delete(id) }
    }
}