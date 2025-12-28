package com.example.thecodecup.domain.usecase.cart

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.repository.CartRepository

/**
 * Use case to update cart item quantity
 */
class UpdateCartItemQuantityUseCase(
    private val cartRepository: CartRepository
) {

    /**
     * Execute the use case to update item quantity
     * @param itemId The cart item ID
     * @param quantity The new quantity (0 or less will remove the item)
     * @return DomainResult indicating success or error
     */
    suspend operator fun invoke(itemId: Int, quantity: Int): DomainResult<Unit> {
        return try {
            if (quantity <= 0) {
                cartRepository.removeItem(itemId)
            } else {
                cartRepository.updateItemQuantity(itemId, quantity)
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to update cart item: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

