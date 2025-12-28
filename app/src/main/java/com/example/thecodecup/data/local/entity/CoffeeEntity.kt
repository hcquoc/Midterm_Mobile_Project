package com.example.thecodecup.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Coffee table
 */
@Entity(tableName = "coffees")
data class CoffeeEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String? = null,
    val category: String,
    val rating: Double = 4.5,
    val reviewCount: Int = 0
)

