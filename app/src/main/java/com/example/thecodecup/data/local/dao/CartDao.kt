package com.example.thecodecup.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.thecodecup.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Cart operations
 */
@Dao
interface CartDao {

    /**
     * Insert a cart item, replacing on conflict
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItemEntity): Long

    /**
     * Update an existing cart item
     */
    @Update
    suspend fun update(cartItem: CartItemEntity)

    /**
     * Delete a cart item
     */
    @Delete
    suspend fun delete(cartItem: CartItemEntity)

    /**
     * Delete cart item by ID
     */
    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteById(itemId: Int)

    /**
     * Clear all items from cart
     */
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    /**
     * Get all cart items as Flow for reactive updates
     */
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getAllItems(): Flow<List<CartItemEntity>>

    /**
     * Get all cart items (suspend version)
     */
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    suspend fun getAllItemsList(): List<CartItemEntity>

    /**
     * Get cart item by ID
     */
    @Query("SELECT * FROM cart_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): CartItemEntity?

    /**
     * Update item quantity by ID
     */
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :itemId")
    suspend fun updateQuantity(itemId: Int, quantity: Int)

    /**
     * Get total item count in cart
     */
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM cart_items")
    suspend fun getTotalItemCount(): Int

    /**
     * Get total price of cart
     */
    @Query("SELECT COALESCE(SUM(unitPrice * quantity), 0.0) FROM cart_items")
    suspend fun getTotalPrice(): Double

    /**
     * Check if cart is empty
     */
    @Query("SELECT COUNT(*) FROM cart_items")
    suspend fun getItemCount(): Int

    /**
     * Find existing cart item with same coffee and options
     */
    @Query("SELECT * FROM cart_items WHERE coffeeId = :coffeeId AND selectedOptions = :options LIMIT 1")
    suspend fun findItemByCoffeeAndOptions(coffeeId: Int, options: String): CartItemEntity?
}

