package com.example.thecodecup.data.repository

import com.example.thecodecup.data.local.dao.OrderDao
import com.example.thecodecup.data.local.entity.OrderEntity
import com.example.thecodecup.data.local.entity.OrderItemEntity
import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.CoffeeOptions
import com.example.thecodecup.domain.model.Ice
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.model.OrderItem
import com.example.thecodecup.domain.model.OrderStatus
import com.example.thecodecup.domain.model.Shot
import com.example.thecodecup.domain.model.Size
import com.example.thecodecup.domain.model.Temperature
import com.example.thecodecup.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.util.UUID

/**
 * Implementation of OrderRepository
 * Uses Room database for persistent storage
 */
class OrderRepositoryImpl(
    private val orderDao: OrderDao
) : OrderRepository {

    override fun observeOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { entity -> entityToOrder(entity) }
        }
    }

    override fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> {
        return orderDao.getOrdersByStatus(status.name).map { entities ->
            entities.map { entity -> entityToOrder(entity) }
        }
    }

    override fun getOngoingOrders(): Flow<List<Order>> {
        return orderDao.getOngoingOrders().map { entities ->
            entities.map { entity -> entityToOrder(entity) }
        }
    }

    override fun getOrderHistory(): Flow<List<Order>> {
        return orderDao.getOrderHistory().map { entities ->
            entities.map { entity -> entityToOrder(entity) }
        }
    }

    override suspend fun getOrderById(orderId: String): Order? {
        val entity = orderDao.getOrderById(orderId) ?: return null
        return entityToOrder(entity)
    }

    override suspend fun createOrder(cart: Cart, address: String): Order {
        // Generate unique order ID
        val orderId = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}"

        // Create OrderEntity
        val orderEntity = OrderEntity(
            id = orderId,
            totalAmount = cart.totalPrice,
            status = OrderStatus.ONGOING.name,
            address = address,
            date = System.currentTimeMillis()
        )

        // Create OrderItemEntities
        val orderItemEntities = cart.items.map { cartItem ->
            OrderItemEntity(
                orderId = orderId,
                coffeeId = cartItem.coffee.id,
                coffeeName = cartItem.coffee.name,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice,
                totalPrice = cartItem.totalPrice,
                selectedOptions = optionsToJson(cartItem.options)
            )
        }

        // Insert into database
        orderDao.insertOrder(orderEntity)
        orderDao.insertOrderItems(orderItemEntities)

        // Return the created order
        return entityToOrder(orderEntity)
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        orderDao.updateOrderStatus(orderId, status.name)
    }

    override suspend fun cancelOrder(orderId: String) {
        orderDao.updateOrderStatus(orderId, OrderStatus.CANCELLED.name)
    }

    // ==================== Private Helper Methods ====================

    /**
     * Convert OrderEntity to Order domain model
     */
    private suspend fun entityToOrder(entity: OrderEntity): Order {
        val itemEntities = orderDao.getOrderItems(entity.id)
        val items = itemEntities.map { itemEntity ->
            OrderItem(
                coffeeId = itemEntity.coffeeId,
                coffeeName = itemEntity.coffeeName,
                options = jsonToOptions(itemEntity.selectedOptions),
                quantity = itemEntity.quantity,
                unitPrice = itemEntity.unitPrice,
                totalPrice = itemEntity.totalPrice
            )
        }

        return Order(
            id = entity.id,
            items = items,
            totalPrice = entity.totalAmount,
            status = try {
                OrderStatus.valueOf(entity.status)
            } catch (_: Exception) {
                OrderStatus.ONGOING
            },
            address = entity.address,
            createdAt = entity.date
        )
    }

    /**
     * Convert CoffeeOptions to JSON string
     */
    private fun optionsToJson(options: CoffeeOptions): String {
        return JSONObject().apply {
            put("shot", options.shot.name)
            put("temperature", options.temperature.name)
            put("size", options.size.name)
            put("ice", options.ice.name)
        }.toString()
    }

    /**
     * Parse JSON string to CoffeeOptions
     */
    private fun jsonToOptions(json: String): CoffeeOptions {
        return try {
            val jsonObject = JSONObject(json)
            CoffeeOptions(
                shot = Shot.valueOf(jsonObject.optString("shot", "SINGLE")),
                temperature = Temperature.valueOf(jsonObject.optString("temperature", "HOT")),
                size = Size.valueOf(jsonObject.optString("size", "MEDIUM")),
                ice = Ice.valueOf(jsonObject.optString("ice", "FULL"))
            )
        } catch (_: Exception) {
            CoffeeOptions()
        }
    }
}

