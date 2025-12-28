package com.example.thecodecup.data.model

/**
 * Entity class for CartItem in data layer
 * Used for data storage/transfer (e.g., Room database, API)
 * Maps to/from domain model CartItem
 */
data class CartItemEntity(
    val id: Int,
    val coffeeId: Int,
    val coffeeName: String,
    val coffeeBasePrice: Double,
    val shot: String,
    val temperature: String,
    val size: String,
    val ice: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

/**
 * Extension to convert domain CartItem to entity
 */
fun com.example.thecodecup.domain.model.CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        id = id,
        coffeeId = coffee.id,
        coffeeName = coffee.name,
        coffeeBasePrice = coffee.basePrice,
        shot = options.shot.name,
        temperature = options.temperature.name,
        size = options.size.name,
        ice = options.ice.name,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice
    )
}

