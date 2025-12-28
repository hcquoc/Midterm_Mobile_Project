package com.example.thecodecup.presentation.common

/**
 * Generic UI State wrapper for handling Loading, Success, and Error states
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>
}

/**
 * Extension function to handle UiState
 */
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) action(data)
    return this
}

inline fun <T> UiState<T>.onError(action: (String) -> Unit): UiState<T> {
    if (this is UiState.Error) action(message)
    return this
}

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> {
    if (this is UiState.Loading) action()
    return this
}

/**
 * Helper to get data or null
 */
fun <T> UiState<T>.getOrNull(): T? = (this as? UiState.Success)?.data

/**
 * Helper to check if loading
 */
val <T> UiState<T>.isLoading: Boolean get() = this is UiState.Loading

/**
 * Helper to check if success
 */
val <T> UiState<T>.isSuccess: Boolean get() = this is UiState.Success

/**
 * Helper to check if error
 */
val <T> UiState<T>.isError: Boolean get() = this is UiState.Error

