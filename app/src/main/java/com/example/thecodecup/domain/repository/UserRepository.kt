package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User operations
 */
interface UserRepository {

    /**
     * Observe current user
     */
    fun observeUser(): Flow<User>

    /**
     * Get current user
     */
    suspend fun getUser(): User

    /**
     * Update user profile
     */
    suspend fun updateUser(user: User)

    /**
     * Update user name
     */
    suspend fun updateName(name: String)

    /**
     * Update user phone
     */
    suspend fun updatePhone(phone: String)

    /**
     * Update user email
     */
    suspend fun updateEmail(email: String)

    /**
     * Update user address
     */
    suspend fun updateAddress(address: String)

    /**
     * Add loyalty stamp
     */
    suspend fun addLoyaltyStamp()

    /**
     * Reset loyalty stamps
     */
    suspend fun resetLoyaltyStamps()

    /**
     * Add reward points
     */
    suspend fun addRewardPoints(points: Int)

    /**
     * Use reward points (returns false if not enough points)
     */
    suspend fun useRewardPoints(points: Int): Boolean

    /**
     * Add a voucher (each voucher is worth 2,000 VND)
     */
    suspend fun addVoucher()

    /**
     * Use a voucher (returns false if no vouchers available)
     */
    suspend fun useVoucher(): Boolean

    /**
     * Get current voucher count
     */
    suspend fun getVoucherCount(): Int
}

