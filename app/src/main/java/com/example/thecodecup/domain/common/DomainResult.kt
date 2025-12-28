package com.example.thecodecup.domain.common

/**
 * A sealed class that represents the result of a domain operation.
 * Can be either Success with data or Error with exception.
 */
sealed class DomainResult<out T> {

    /**
     * Represents a successful operation with data
     */
    data class Success<out T>(val data: T) : DomainResult<T>()

    /**
     * Represents a failed operation with exception
     */
    data class Error(val exception: DomainException) : DomainResult<Nothing>()

    /**
     * Returns true if this result is a Success
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this result is an Error
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns the data if Success, or null if Error
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    /**
     * Returns the exception if Error, or null if Success
     */
    fun exceptionOrNull(): DomainException? = when (this) {
        is Success -> null
        is Error -> exception
    }

    /**
     * Transform success data using the given function
     */
    inline fun <R> map(transform: (T) -> R): DomainResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    /**
     * Transform success data using the given function that returns DomainResult
     */
    inline fun <R> flatMap(transform: (T) -> DomainResult<R>): DomainResult<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
    }

    /**
     * Execute action if Success
     */
    inline fun onSuccess(action: (T) -> Unit): DomainResult<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Execute action if Error
     */
    inline fun onError(action: (DomainException) -> Unit): DomainResult<T> {
        if (this is Error) action(exception)
        return this
    }

    companion object {
        /**
         * Wrap a suspend function call in a DomainResult
         */
        inline fun <T> runCatching(block: () -> T): DomainResult<T> {
            return try {
                Success(block())
            } catch (e: DomainException) {
                Error(e)
            } catch (e: Exception) {
                Error(DomainException.UnknownException(e.message ?: "Unknown error", e))
            }
        }
    }
}
