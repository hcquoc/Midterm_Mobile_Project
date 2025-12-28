package com.example.thecodecup.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for CartItem table
 * selectedOptions is stored as JSON string for flexibility
 */
@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val coffeeId: Int,
    val coffeeName: String,
    val coffeeBasePrice: Double,
    val quantity: Int,
    /**
     * JSON format: {"shot":"SINGLE","temperature":"ICED","size":"MEDIUM","ice":"FULL"}
     */
    val selectedOptions: String,
    /**
     * Pre-calculated unit price (basePrice + options extra)
     */
    val unitPrice: Double
)

