package com.example.thecodecup.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for OrderItem table
 * Linked to OrderEntity via orderId foreign key
 */
@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val coffeeId: Int,
    val coffeeName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    /**
     * JSON format: {"shot":"SINGLE","temperature":"ICED","size":"MEDIUM","ice":"FULL"}
     */
    val selectedOptions: String
)

