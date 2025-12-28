package com.example.thecodecup.core.result

/**
 * Sealed class representing the result of an operation.
 * Used to wrap responses from repositories and use cases.
 */
sealed class AppResult<out T> {

    /**
     * Represents a successful result with data
     */
    data class Success<T>(val data: T) : AppResult<T>()

    /**
     * Represents a failed result with error
     */
    data class Error(val error: AppError) : AppResult<Nothing>()

    /**
     * Represents a loading state
     */
    data object Loading : AppResult<Nothing>()

    /**
     * Returns true if this is a Success
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this is an Error
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this is Loading
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns the data if Success, null otherwise
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Returns the error if Error, null otherwise
     */
    fun errorOrNull(): AppError? = (this as? Error)?.error

    /**
     * Execute action if Success
     */
    inline fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Execute action if Error
     */
    inline fun onError(action: (AppError) -> Unit): AppResult<T> {
        if (this is Error) action(error)
        return this
    }

    /**
     * Execute action if Loading
     */
    inline fun onLoading(action: () -> Unit): AppResult<T> {
        if (this is Loading) action()
        return this
    }

    /**
     * Map the success data to another type
     */
    inline fun <R> map(transform: (T) -> R): AppResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> Loading
        }
    }
}

