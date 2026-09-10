package com.lifeforge.os.presentation

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Content<out T>(val data: T) : UiState<T>
}

fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Content)?.data