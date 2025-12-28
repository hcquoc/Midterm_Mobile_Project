package com.example.thecodecup.domain.usecase.order

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.repository.CartRepository
import com.example.thecodecup.domain.repository.OrderRepository
import com.example.thecodecup.domain.repository.UserRepository

/**
 * Use case to place a new order
 *
 * This is a complex use case that performs multiple operations:
 * 1. Retrieves items from the Cart
 * 2. Creates an Order with OrderItems
 * 3. Saves Order to DB
 * 4. Clears the Cart
 * 5. Updates User's Loyalty Points (1 point per 1000 currency units)
 */
class PlaceOrderUseCase(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {

    /**
     * Data class for place order result
     */
    data class PlaceOrderResult(
        val order: Order,
        val pointsEarned: Int
    )

    /**
     * Execute the use case to place an order
     *
     * @param note Optional note for the order
     * @param deliveryAddress Optional delivery address (if null, uses user's default address)
     * @return DomainResult containing PlaceOrderResult or error
     */
    suspend operator fun invoke(
        note: String? = null,
        deliveryAddress: String? = null
    ): DomainResult<PlaceOrderResult> {
        return try {
            // Step 1: Get current cart
            val cart = cartRepository.getCart()

            // Validate cart is not empty
            if (cart.isEmpty) {
                return DomainResult.Error(
                    DomainException.EmptyCartException(
                        message = "Cannot place order. Cart is empty."
                    )
                )
            }

            // Step 2: Calculate total price
            val totalPrice = cart.totalPrice

            // Step 3: Get current user for address
            val currentUser = userRepository.getUser()
            val address = deliveryAddress ?: currentUser.address

            // Validate address
            if (address.isBlank()) {
                return DomainResult.Error(
                    DomainException.ValidationException(
                        message = "Delivery address cannot be empty",
                        field = "address"
                    )
                )
            }

            // Step 4: Create order via repository
            // The repository handles creating Order and OrderItems from Cart
            val order = orderRepository.createOrder(cart, address)

            // Step 5: Clear the cart
            cartRepository.clearCart()

            // Step 6: Calculate loyalty points (1 point per 1000 VND)
            val pointsEarned = calculateLoyaltyPoints(totalPrice)

            // Step 7: Add points to user
            if (pointsEarned > 0) {
                userRepository.addRewardPoints(pointsEarned)
            }

            // Step 8: Add 1 loyalty stamp per order (clamped at max 8)
            userRepository.addLoyaltyStamp()

            // Return success with order and points earned
            DomainResult.Success(
                PlaceOrderResult(
                    order = order,
                    pointsEarned = pointsEarned
                )
            )

        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.OrderCreationException(
                    message = "Failed to place order: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Calculate loyalty points based on total price
     * Formula: 1 point per 1000 VND (rounded down)
     *
     * @param totalPrice The total order price in VND
     * @return Number of points earned
     */
    private fun calculateLoyaltyPoints(totalPrice: Double): Int {
        return (totalPrice / 1000).toInt()
    }
}

