package com.example.thecodecup.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thecodecup.data.local.entity.OrderEntity
import com.example.thecodecup.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Order operations
 */
@Dao
interface OrderDao {

    // ==================== Order Operations ====================

    /**
     * Insert a new order and return the row ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    /**
     * Insert multiple order items
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    /**
     * Get all orders as Flow for reactive updates
     */
    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    /**
     * Get orders by status
     */
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY date DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    /**
     * Get ongoing orders (status = ON_GOING or PLACED)
     */
    @Query("SELECT * FROM orders WHERE status IN ('ON_GOING', 'PLACED', 'ONGOING') ORDER BY date DESC")
    fun getOngoingOrders(): Flow<List<OrderEntity>>

    /**
     * Get completed orders (history)
     */
    @Query("SELECT * FROM orders WHERE status IN ('COMPLETED', 'CANCELLED') ORDER BY date DESC")
    fun getOrderHistory(): Flow<List<OrderEntity>>

    /**
     * Get order by ID
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    /**
     * Get order items for a specific order
     */
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    /**
     * Get order items as Flow
     */
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun observeOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    /**
     * Update order status
     */
    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    /**
     * Delete order and its items (items will be deleted via CASCADE)
     */
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: String)

    /**
     * Get last inserted order ID
     */
    @Query("SELECT id FROM orders ORDER BY date DESC LIMIT 1")
    suspend fun getLastOrderId(): String?
}

