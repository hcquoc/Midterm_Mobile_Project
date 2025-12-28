package com.example.thecodecup.domain.usecase.cart

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.repository.CartRepository

/**
 * Use case to remove an item from the cart
 */
class RemoveFromCartUseCase(
    private val cartRepository: CartRepository
) {

    /**
     * Execute the use case to remove an item from cart
     * @param itemId The ID of the cart item to remove
     * @return DomainResult indicating success or error
     */
    suspend operator fun invoke(itemId: Int): DomainResult<Unit> {
        return try {
            cartRepository.removeItem(itemId)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to remove item from cart: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

