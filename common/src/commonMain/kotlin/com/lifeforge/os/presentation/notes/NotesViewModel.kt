package com.lifeforge.os.presentation.notes

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.Note
import com.lifeforge.os.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class NotesState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class NotesViewModel(
    private val noteRepository: NoteRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(NotesState())
    val state: StateFlow<NotesState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            noteRepository.observeAll()
                .catch { e ->
                    _state.value = NotesState(isLoading = false, error = e.message)
                }
                .collect { notes ->
                    _state.value = NotesState(notes = notes.sortedByDescending { it.updatedAt }, isLoading = false, error = null)
                }
        }
    }

    fun addNote(content: String) {
        if (content.isBlank()) return
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            noteRepository.save(
                Note(
                    id = newEntityId(),
                    plainText = content.trim(),
                    content = content.trim(),
                    createdAt = now,
                    updatedAt = now,
                )
            )
        }
    }

    fun togglePinned(id: String) {
        scopeProvider.ioScope.launch { noteRepository.togglePinned(id) }
    }

    fun deleteNote(id: String) {
        scopeProvider.ioScope.launch { noteRepository.delete(id) }
    }
}