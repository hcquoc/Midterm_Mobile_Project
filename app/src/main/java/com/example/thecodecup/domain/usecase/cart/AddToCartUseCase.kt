package com.example.thecodecup.domain.usecase.cart

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeOptions
import com.example.thecodecup.domain.repository.CartRepository

/**
 * Use case to add an item to the cart
 */
class AddToCartUseCase(
    private val cartRepository: CartRepository
) {

    /**
     * Execute the use case to add a coffee to cart
     * @param coffee The coffee to add
     * @param options The customization options
     * @param quantity The quantity to add
     * @return DomainResult containing the added CartItem or an error
     */
    suspend operator fun invoke(
        coffee: Coffee,
        options: CoffeeOptions,
        quantity: Int
    ): DomainResult<CartItem> {
        // Validate input
        if (quantity <= 0) {
            return DomainResult.Error(
                DomainException.InvalidQuantityException(
                    quantity = quantity,
                    message = "Quantity must be greater than 0"
                )
            )
        }

        return try {
            val cartItem = cartRepository.addItem(coffee, options, quantity)
            DomainResult.Success(cartItem)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to add item to cart: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

