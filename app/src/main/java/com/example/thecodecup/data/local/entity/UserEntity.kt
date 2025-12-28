package com.example.thecodecup.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for User table - stores user information and loyalty program data
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String? = null,
    val loyaltyStamps: Int = 0,  // Tracks the 8 cups, max 8
    val loyaltyPoints: Int = 0   // Tracks the total points
)
