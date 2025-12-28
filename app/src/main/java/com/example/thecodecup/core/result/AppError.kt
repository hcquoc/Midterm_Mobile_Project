package com.example.thecodecup.core.result

/**
 * Represents different types of errors in the application
 */
sealed class AppError(
    open val message: String,
    open val throwable: Throwable? = null
) {
    /**
     * Network related errors
     */
    data class NetworkError(
        override val message: String = "Network error occurred",
        override val throwable: Throwable? = null
    ) : AppError(message, throwable)

    /**
     * Server/API errors
     */
    data class ServerError(
        val code: Int,
        override val message: String,
        override val throwable: Throwable? = null
    ) : AppError(message, throwable)

    /**
     * Local database errors
     */
    data class DatabaseError(
        override val message: String = "Database error occurred",
        override val throwable: Throwable? = null
    ) : AppError(message, throwable)

    /**
     * Validation errors (e.g., invalid input)
     */
    data class ValidationError(
        override val message: String,
        val field: String? = null
    ) : AppError(message)

    /**
     * Authentication errors
     */
    data class AuthError(
        override val message: String = "Authentication required",
        override val throwable: Throwable? = null
    ) : AppError(message, throwable)

    /**
     * Unknown/unexpected errors
     */
    data class UnknownError(
        override val message: String = "An unexpected error occurred",
        override val throwable: Throwable? = null
    ) : AppError(message, throwable)

    companion object {
        /**
         * Create an AppError from a Throwable
         */
        fun fromThrowable(throwable: Throwable): AppError {
            return UnknownError(
                message = throwable.message ?: "An unexpected error occurred",
                throwable = throwable
            )
        }
    }
}

