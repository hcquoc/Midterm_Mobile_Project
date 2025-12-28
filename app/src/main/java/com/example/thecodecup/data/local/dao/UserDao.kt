package com.example.thecodecup.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.thecodecup.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>

    @Query("UPDATE users SET loyaltyStamps = :stamps, loyaltyPoints = :points WHERE id = :userId")
    suspend fun updateLoyaltyData(userId: String, stamps: Int, points: Int)

    @Query("UPDATE users SET loyaltyStamps = :stamps WHERE id = :userId")
    suspend fun updateLoyaltyStamps(userId: String, stamps: Int)

    @Query("SELECT loyaltyStamps FROM users WHERE id = :userId")
    fun observeLoyaltyStamps(userId: String): Flow<Int?>

    @Query("SELECT loyaltyPoints FROM users WHERE id = :userId")
    fun observeLoyaltyPoints(userId: String): Flow<Int?>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
