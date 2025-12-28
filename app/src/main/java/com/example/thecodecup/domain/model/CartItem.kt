package com.example.thecodecup.domain.model

import kotlin.math.roundToLong

/**
 * Domain model for Cart Item
 *
 * Prices are calculated using Long arithmetic to avoid floating-point
 * precision errors with VND currency.
 */
data class CartItem(
    val id: Int,
    val coffee: Coffee,
    val options: CoffeeOptions,
    val quantity: Int
) {
    /**
     * Calculate unit price for this item (base + options)
     * Uses Long arithmetic for precision, returns Double for compatibility
     */
    val unitPrice: Double
        get() {
            val baseLong = coffee.basePrice.roundToLong()
            val extrasLong = options.calculateExtraPrice().roundToLong()
            return (baseLong + extrasLong).toDouble()
        }

    /**
     * Calculate total price for this item (unitPrice * quantity)
     * Uses Long arithmetic for precision
     */
    val totalPrice: Double
        get() {
            val baseLong = coffee.basePrice.roundToLong()
            val extrasLong = options.calculateExtraPrice().roundToLong()
            val unitPriceLong = baseLong + extrasLong
            return (unitPriceLong * quantity).toDouble()
        }

    /**
     * Returns a formatted string of the customization details
     */
    fun getDetailsString(): String = options.toDisplayString()
}

/**
 * Domain model for Cart
 *
 * Prices are calculated using Long arithmetic to avoid floating-point
 * precision errors with VND currency.
 */
data class Cart(
    val items: List<CartItem> = emptyList()
) {
    /**
     * Calculate total price of all items in cart
     * Uses Long arithmetic for precision
     */
    val totalPrice: Double
        get() {
            val totalLong = items.sumOf { item ->
                val baseLong = item.coffee.basePrice.roundToLong()
                val extrasLong = item.options.calculateExtraPrice().roundToLong()
                (baseLong + extrasLong) * item.quantity
            }
            return totalLong.toDouble()
        }

    val itemCount: Int
        get() = items.sumOf { it.quantity }

    val isEmpty: Boolean
        get() = items.isEmpty()
}
