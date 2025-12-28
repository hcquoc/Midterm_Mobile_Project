package com.example.thecodecup.domain.model

/**
 * Domain model for Order
 */
data class Order(
    val id: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val address: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    val formattedDate: String
        get() {
            val sdf = java.text.SimpleDateFormat("dd MMMM | hh:mm a", java.util.Locale.US)
            return sdf.format(java.util.Date(createdAt))
        }
}

/**
 * Domain model for an item in an order
 */
data class OrderItem(
    val coffeeId: Int,
    val coffeeName: String,
    val options: CoffeeOptions,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
) {
    fun getDetailsString(): String = options.toDisplayString()
}

/**
 * Order status enum
 */
enum class OrderStatus {
    PLACED,     // Order just placed
    ONGOING,    // Order being prepared/delivered
    COMPLETED,  // Order delivered
    CANCELLED   // Order cancelled
}

