package com.lifeforge.os.presentation.courses

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.Course
import com.lifeforge.os.domain.model.CourseStatus
import com.lifeforge.os.domain.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class CoursesState(
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class CoursesViewModel(
    private val courseRepository: CourseRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(CoursesState())
    val state: StateFlow<CoursesState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            courseRepository.observeAll()
                .catch { e ->
                    _state.value = CoursesState(isLoading = false, error = e.message)
                }
                .collect { courses ->
                    _state.value = CoursesState(courses = courses, isLoading = false, error = null)
                }
        }
    }

    fun addCourse(title: String, provider: String) {
        if (title.isBlank()) return
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            courseRepository.save(
                Course(
                    id = newEntityId(),
                    title = title.trim(),
                    provider = provider.trim().ifBlank { null },
                    createdAt = now,
                    updatedAt = now,
                )
            )
        }
    }

    fun bumpProgress(id: String) {
        scopeProvider.ioScope.launch {
            val course = courseRepository.getById(id) ?: return@launch
            val next = (course.progressPercent + 0.1).coerceAtMost(1.0)
            val status = if (next >= 1.0) CourseStatus.Completed else course.status
            courseRepository.save(course.copy(progressPercent = next, status = status, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteCourse(id: String) {
        scopeProvider.ioScope.launch { courseRepository.delete(id) }
    }
}