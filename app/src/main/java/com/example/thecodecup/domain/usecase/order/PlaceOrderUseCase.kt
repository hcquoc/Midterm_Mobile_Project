package com.example.thecodecup.domain.usecase.order

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.repository.CartRepository
import com.example.thecodecup.domain.repository.OrderRepository
import com.example.thecodecup.domain.repository.UserRepository
import kotlin.math.roundToInt

/**
 * Use case to place a new order
 *
 * This is a complex use case that performs multiple operations:
 * 1. Retrieves items from the Cart
 * 2. Creates an Order with OrderItems
 * 3. Saves Order to DB
 * 4. Clears the Cart
 * 5. Deducts points if "Pay with Points" is enabled
 * 6. Updates User's Loyalty Points with tier multiplier (Silver 1x, Gold 1.5x)
 */
class PlaceOrderUseCase(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {
    companion object {
        /** Conversion rate: 1 Point = 100 VND */
        const val POINTS_TO_VND_RATE = 100.0
    }

    /**
     * Data class for place order result
     */
    data class PlaceOrderResult(
        val order: Order,
        val pointsEarned: Int,
        val pointsUsed: Int = 0,
        val discountAmount: Double = 0.0
    )

    /**
     * Execute the use case to place an order
     *
     * @param note Optional note for the order
     * @param deliveryAddress Optional delivery address (if null, uses user's default address)
     * @param usePoints Whether to use points for discount
     * @param pointsToUse Number of points to use (if usePoints is true)
     * @return DomainResult containing PlaceOrderResult or error
     */
    suspend operator fun invoke(
        note: String? = null,
        deliveryAddress: String? = null,
        usePoints: Boolean = false,
        pointsToUse: Int = 0
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

            // Step 2: Get current user
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

            // Step 3: Calculate discount from points
            var actualPointsUsed = 0
            var discountAmount = 0.0

            if (usePoints && pointsToUse > 0) {
                // Validate user has enough points
                if (pointsToUse > currentUser.rewardPoints) {
                    return DomainResult.Error(
                        DomainException.ValidationException(
                            message = "Not enough points. You have ${currentUser.rewardPoints} points.",
                            field = "points"
                        )
                    )
                }

                // Calculate discount (1 point = 100 VND)
                val maxDiscount = pointsToUse * POINTS_TO_VND_RATE
                // Don't allow discount more than cart total
                discountAmount = minOf(maxDiscount, cart.totalPrice)
                actualPointsUsed = (discountAmount / POINTS_TO_VND_RATE).toInt()
            }

            // Step 4: Create order via repository
            val order = orderRepository.createOrder(cart, address)

            // Step 5: Clear the cart
            cartRepository.clearCart()

            // Step 6: Deduct points if used
            if (actualPointsUsed > 0) {
                userRepository.useRewardPoints(actualPointsUsed)
            }

            // Step 7: Calculate loyalty points with tier multiplier
            // Base points: 1 point per item
            val basePointsEarned = calculateBasePoints(cart)
            // Apply tier multiplier (Silver 1x, Gold 1.5x)
            val multiplier = currentUser.pointsMultiplier
            val pointsEarned = (basePointsEarned * multiplier).roundToInt()

            // Step 8: Add earned points to user
            if (pointsEarned > 0) {
                userRepository.addRewardPoints(pointsEarned)
            }

            // Step 9: Add loyalty stamp for each item purchased
            cart.items.forEach { _ ->
                userRepository.addLoyaltyStamp()
            }

            // Return success with order and points info
            DomainResult.Success(
                PlaceOrderResult(
                    order = order,
                    pointsEarned = pointsEarned,
                    pointsUsed = actualPointsUsed,
                    discountAmount = discountAmount
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
     * Calculate base loyalty points (before multiplier)
     * Formula: 1 point per coffee cup (regardless of price)
     *
     * @param cart The cart containing items
     * @return Number of base points earned (total quantity of items)
     */
    private fun calculateBasePoints(cart: com.example.thecodecup.domain.model.Cart): Int {
        return cart.items.sumOf { it.quantity }
    }
}

