package com.example.thecodecup.domain.usecase.cart

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.domain.repository.CartRepository
import kotlin.math.roundToLong

/**
 * Use case to calculate cart total price
 *
 * Uses Long arithmetic internally to avoid floating-point precision errors
 * with VND currency (which has no decimal places).
 */
class CalculateCartTotalUseCase(
    private val cartRepository: CartRepository
) {

    companion object {
        // Delivery fee in VND (15,000đ for orders, 0 for empty cart)
        const val DELIVERY_FEE_VND: Long = 15000L
    }

    /**
     * Data class containing cart total information
     * All prices are in VND (Double for compatibility, but represent whole numbers)
     */
    data class CartTotalInfo(
        val subtotal: Double,
        val deliveryFee: Double,
        val total: Double,
        val itemCount: Int
    )

    /**
     * Execute the use case to calculate cart total
     * @return DomainResult containing CartTotalInfo
     */
    suspend operator fun invoke(): DomainResult<CartTotalInfo> {
        return try {
            val cart = cartRepository.getCart()
            val totalInfo = calculateTotal(cart)
            DomainResult.Success(totalInfo)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to calculate cart total: ${e.message}",
                    cause = e
                )
            )
        }
    }

    /**
     * Calculate total from cart items using Long arithmetic for precision
     * Logic: sum of (coffee basePrice + options extra) * quantity for each item
     */
    private fun calculateTotal(cart: Cart): CartTotalInfo {
        // Calculate each item total as Long to avoid floating-point errors
        val itemTotals = cart.items.map { item ->
            calculateItemTotalAsLong(item)
        }

        // Sum all item totals (Long arithmetic - no precision loss)
        val subtotalLong = itemTotals.sum()

        // Delivery fee (0 if empty cart)
        val deliveryFeeLong = if (cart.isEmpty) 0L else DELIVERY_FEE_VND

        // Total = subtotal + delivery
        val totalLong = subtotalLong + deliveryFeeLong

        // Convert back to Double for API compatibility
        return CartTotalInfo(
            subtotal = subtotalLong.toDouble(),
            deliveryFee = deliveryFeeLong.toDouble(),
            total = totalLong.toDouble(),
            itemCount = cart.itemCount
        )
    }

    /**
     * Calculate total for a single cart item using Long arithmetic
     * Formula: round(basePrice) + round(optionsExtra)) * quantity
     *
     * This ensures no floating-point precision errors occur
     */
    private fun calculateItemTotalAsLong(item: CartItem): Long {
        // Round each component to Long first (VND has no decimals)
        val basePriceLong = item.coffee.basePrice.roundToLong()
        val optionsExtraLong = item.options.calculateExtraPrice().roundToLong()

        // Unit price = base + extras
        val unitPriceLong = basePriceLong + optionsExtraLong

        // Total = unit price * quantity
        return unitPriceLong * item.quantity
    }
}
