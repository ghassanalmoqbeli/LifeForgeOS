package com.lifeforge.os.presentation.books

import com.lifeforge.os.core.utils.CoroutineScopeProvider
import com.lifeforge.os.core.utils.newEntityId
import com.lifeforge.os.domain.model.Book
import com.lifeforge.os.domain.model.BookStatus
import com.lifeforge.os.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class BooksState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class BooksViewModel(
    private val bookRepository: BookRepository,
    private val scopeProvider: CoroutineScopeProvider,
) {
    private val _state = MutableStateFlow(BooksState())
    val state: StateFlow<BooksState> = _state.asStateFlow()

    init {
        scopeProvider.defaultScope.launch {
            bookRepository.observeAll()
                .catch { e ->
                    _state.value = BooksState(isLoading = false, error = e.message)
                }
                .collect { books ->
                    _state.value = BooksState(books = books, isLoading = false, error = null)
                }
        }
    }

    fun addBook(title: String, author: String) {
        if (title.isBlank()) return
        val now = System.currentTimeMillis()
        scopeProvider.ioScope.launch {
            bookRepository.save(
                Book(
                    id = newEntityId(),
                    title = title.trim(),
                    author = author.trim().ifBlank { "—" },
                    createdAt = now,
                    updatedAt = now,
                )
            )
        }
    }

    fun cycleStatus(id: String) {
        scopeProvider.ioScope.launch {
            val book = bookRepository.getById(id) ?: return@launch
            val next = when (book.status) {
                BookStatus.WantToRead -> BookStatus.Reading
                BookStatus.Reading -> BookStatus.Completed
                BookStatus.Completed -> BookStatus.WantToRead
                BookStatus.Paused -> BookStatus.Reading
                BookStatus.Dropped -> BookStatus.WantToRead
            }
            val progress = if (next == BookStatus.Completed) 1.0 else book.progressPercent
            bookRepository.save(book.copy(status = next, progressPercent = progress, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteBook(id: String) {
        scopeProvider.ioScope.launch { bookRepository.delete(id) }
    }
}