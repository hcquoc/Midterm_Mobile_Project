package com.example.thecodecup.presentation.order

import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.model.OrderStatus
import com.example.thecodecup.presentation.utils.PriceFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Display model for order items in list
 */
data class OrderDisplayItem(
    val id: String,
    val coffeeName: String,
    val date: String,
    val address: String,
    val price: Double,
    val status: OrderStatus = OrderStatus.ONGOING,
    val itemCount: Int = 1,
    val formattedPrice: String = ""
) {
    companion object {
        /**
         * Convert domain Order to OrderDisplayItem
         */
        fun fromOrder(order: Order): OrderDisplayItem {
            // Format date nicely
            val dateFormat = SimpleDateFormat("dd MMM | hh:mm a", Locale.US)
            val formattedDate = dateFormat.format(Date(order.createdAt))

            // Get first item name or summary
            val coffeeName = when {
                order.items.isEmpty() -> "No items"
                order.items.size == 1 -> order.items.first().coffeeName
                else -> "${order.items.first().coffeeName} +${order.items.size - 1} more"
            }

            return OrderDisplayItem(
                id = order.id,
                coffeeName = coffeeName,
                date = formattedDate,
                address = order.address,
                price = order.totalPrice,
                status = order.status,
                itemCount = order.items.size,
                formattedPrice = PriceFormatter.formatVND(order.totalPrice)
            )
        }
    }
}

