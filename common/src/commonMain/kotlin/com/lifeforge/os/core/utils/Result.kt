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

    fun <R> map(transform: (T) -> R): Result<R> =
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
    val message: String
    data class Network(override val message: String, val cause: Throwable? = null) : AppError
    data class Database(override val message: String, val cause: Throwable? = null) : AppError
    data class Validation(val field: String?, override val message: String) : AppError
    data class Auth(override val message: String, val cause: Throwable? = null) : AppError
    data class SyncConflict(override val message: String, val entityType: String, val entityId: String) : AppError
    data class File(override val message: String, val cause: Throwable? = null) : AppError
    data class Import(override val message: String, val cause: Throwable? = null) : AppError
    data class Permission(override val message: String) : AppError
    data class Storage(override val message: String, val cause: Throwable? = null) : AppError
    data class Unknown(override val message: String, val cause: Throwable? = null) : AppError
}

fun Throwable.toAppError(): AppError = AppError.Unknown(message ?: "Unknown error", this)

fun Result<Nothing>.orError(message: String): AppError = AppError.Unknown(message)