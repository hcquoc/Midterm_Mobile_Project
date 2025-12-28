package com.example.thecodecup.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Order table
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val totalAmount: Double,
    val status: String, // "ON_GOING", "COMPLETED", "CANCELLED", "PLACED"
    val address: String,
    val date: Long = System.currentTimeMillis()
)

