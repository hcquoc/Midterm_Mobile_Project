package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeOptions
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Cart operations
 */
interface CartRepository {

    /**
     * Observe cart state
     */
    fun observeCart(): Flow<Cart>

    /**
     * Get current cart
     */
    suspend fun getCart(): Cart

    /**
     * Add item to cart
     */
    suspend fun addItem(coffee: Coffee, options: CoffeeOptions, quantity: Int): CartItem

    /**
     * Update item quantity
     */
    suspend fun updateItemQuantity(itemId: Int, quantity: Int)

    /**
     * Remove item from cart
     */
    suspend fun removeItem(itemId: Int)

    /**
     * Clear all items from cart
     */
    suspend fun clearCart()

    /**
     * Get total item count
     */
    suspend fun getItemCount(): Int

    /**
     * Get total price
     */
    suspend fun getTotalPrice(): Double
}

