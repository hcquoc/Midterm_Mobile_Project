package com.example.thecodecup.domain.common

/**
 * Sealed class representing domain-level exceptions.
 * These exceptions are used throughout the domain layer for error handling.
 */
sealed class DomainException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    // ==================== Database Exceptions ====================

    /**
     * Exception when database operation fails
     */
    class DatabaseException(
        message: String = "Database operation failed",
        cause: Throwable? = null
    ) : DomainException(message, cause)

    // ==================== Cart Exceptions ====================

    /**
     * Exception when cart item is not found
     */
    class CartItemNotFoundException(
        val itemId: Int,
        message: String = "Cart item with id $itemId not found"
    ) : DomainException(message)

    /**
     * Exception when trying to add invalid quantity
     */
    class InvalidQuantityException(
        val quantity: Int,
        message: String = "Invalid quantity: $quantity. Must be greater than 0"
    ) : DomainException(message)

    /**
     * Exception when cart is empty
     */
    class EmptyCartException(
        message: String = "Cart is empty"
    ) : DomainException(message)

    // ==================== Coffee Exceptions ====================

    /**
     * Exception when coffee is not found
     */
    class CoffeeNotFoundException(
        val coffeeId: Int,
        message: String = "Coffee with id $coffeeId not found"
    ) : DomainException(message)

    // ==================== Order Exceptions ====================

    /**
     * Exception when order is not found
     */
    class OrderNotFoundException(
        val orderId: String,
        message: String = "Order with id $orderId not found"
    ) : DomainException(message)

    /**
     * Exception when order creation fails
     */
    class OrderCreationException(
        message: String = "Failed to create order",
        cause: Throwable? = null
    ) : DomainException(message, cause)

    // ==================== User Exceptions ====================

    /**
     * Exception when user is not found
     */
    class UserNotFoundException(
        val userId: String,
        message: String = "User with id $userId not found"
    ) : DomainException(message)

    /**
     * Exception when user doesn't have enough points
     */
    class InsufficientPointsException(
        val required: Int,
        val available: Int,
        message: String = "Insufficient points. Required: $required, Available: $available"
    ) : DomainException(message)

    // ==================== Validation Exceptions ====================

    /**
     * Exception for general validation errors
     */
    class ValidationException(
        message: String,
        val field: String? = null
    ) : DomainException(message)

    // ==================== Network Exceptions ====================

    /**
     * Exception when network is unavailable
     */
    class NetworkException(
        message: String = "Network unavailable",
        cause: Throwable? = null
    ) : DomainException(message, cause)

    // ==================== Unknown Exception ====================

    /**
     * Exception for unexpected errors
     */
    class UnknownException(
        message: String = "An unknown error occurred",
        cause: Throwable? = null
    ) : DomainException(message, cause)
}
