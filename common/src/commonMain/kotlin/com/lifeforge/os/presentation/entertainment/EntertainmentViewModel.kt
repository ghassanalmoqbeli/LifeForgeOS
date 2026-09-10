package com.lifeforge.os.presentation.entertainment

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.MediaItem
import com.lifeforge.os.domain.model.MediaStatus
import com.lifeforge.os.domain.model.MediaType
import com.lifeforge.os.domain.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class EntertainmentState(
    val items: List<MediaItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class EntertainmentViewModel(
    private val mediaRepository: MediaRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(EntertainmentState())
    val state: StateFlow<EntertainmentState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            mediaRepository.observeAll()
                .catch { e ->
                    _state.value = EntertainmentState(isLoading = false, error = e.message)
                }
                .collect { items ->
                    _state.value = EntertainmentState(items = items, isLoading = false, error = null)
                }
        }
    }

    fun addItem(title: String) {
        if (title.isBlank()) return
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            mediaRepository.save(
                MediaItem(
                    id = newEntityId(),
                    type = MediaType.Movie,
                    title = title.trim(),
                    createdAt = now,
                    updatedAt = now,
                )
            )
        }
    }

    fun cycleStatus(id: String) {
        scopeProvider.ioScope.launch {
            val item = mediaRepository.getById(id) ?: return@launch
            val next = when (item.status) {
                MediaStatus.WantToWatch -> MediaStatus.Watching
                MediaStatus.Watching -> MediaStatus.Completed
                MediaStatus.Completed -> MediaStatus.WantToWatch
                MediaStatus.Paused -> MediaStatus.Watching
                MediaStatus.Dropped -> MediaStatus.WantToWatch
            }
            val progress = if (next == MediaStatus.Completed) 1.0 else item.progressPercent
            mediaRepository.save(item.copy(status = next, progressPercent = progress, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteItem(id: String) {
        scopeProvider.ioScope.launch { mediaRepository.delete(id) }
    }
}