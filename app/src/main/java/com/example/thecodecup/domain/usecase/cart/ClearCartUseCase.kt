package com.example.thecodecup.domain.usecase.cart

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.repository.CartRepository

/**
 * Use case to clear all items from the cart
 */
class ClearCartUseCase(
    private val cartRepository: CartRepository
) {

    /**
     * Execute the use case to clear the cart
     * @return DomainResult indicating success or error
     */
    suspend operator fun invoke(): DomainResult<Unit> {
        return try {
            cartRepository.clearCart()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to clear cart: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

