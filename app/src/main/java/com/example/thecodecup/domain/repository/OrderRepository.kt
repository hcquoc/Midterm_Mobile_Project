package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Order operations
 */
interface OrderRepository {

    /**
     * Observe all orders
     */
    fun observeOrders(): Flow<List<Order>>

    /**
     * Get orders by status
     */
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>

    /**
     * Get ongoing orders
     */
    fun getOngoingOrders(): Flow<List<Order>>

    /**
     * Get completed orders (history)
     */
    fun getOrderHistory(): Flow<List<Order>>

    /**
     * Get order by ID
     */
    suspend fun getOrderById(orderId: String): Order?

    /**
     * Create a new order from cart
     */
    suspend fun createOrder(cart: Cart, address: String): Order

    /**
     * Update order status
     */
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)

    /**
     * Cancel an order
     */
    suspend fun cancelOrder(orderId: String)
}

