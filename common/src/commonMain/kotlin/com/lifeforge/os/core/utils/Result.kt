package com.lifeforge.os.core.utils

/**
 * Unified Result type for repository/use case boundaries.
 * Avoids platform coupling and provides a common error model.
 */
sealed interface Result<out T> {
    data class Success<out T>(val value: T) : Result<T>
    data class Failure(val error: AppError) : Result<Nothing>

    val isSuccess: Boolean
        get() = this is Success

    fun getOrNull(): T? = (this as? Success)?.value

    fun getError(): AppError? = (this as? Failure)?.error

    inline fun <R> map(transform: (T) -> R): Result<R> =
        when (this) {
            is Success -> Result.Success(transform(value))
            is Failure -> this
        }
}

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) block(value)
    return this
}

inline fun <T> Result<T>.onFailure(block: (AppError) -> Unit): Result<T> {
    if (this is Result.Failure) block(error)
    return this
}

/**
 * Structured error model. Every error maps to a user-friendly message.
 */
sealed interface AppError {
    data class Network(val message: String, val cause: Throwable? = null) : AppError
    data class Database(val message: String, val cause: Throwable? = null) : AppError
    data class Validation(val field: String?, val message: String) : AppError
    data class Auth(val message: String, val cause: Throwable? = null) : AppError
    data class SyncConflict(val message: String, val entityType: String, val entityId: String) : AppError
    data class File(val message: String, val cause: Throwable? = null) : AppError
    data class Import(val message: String, val cause: Throwable? = null) : AppError
    data class Permission(val message: String) : AppError
    data class Storage(val message: String, val cause: Throwable? = null) : AppError
    data class Unknown(val message: String, val cause: Throwable? = null) : AppError
}

fun Throwable.toAppError(): AppError = AppError.Unknown(message ?: "Unknown error", this)

fun Result<Nothing>.orError(message: String): AppError = AppError.Unknown(message)