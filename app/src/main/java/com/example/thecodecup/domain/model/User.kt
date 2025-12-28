package com.example.thecodecup.domain.model

import java.util.Locale

/**
 * Domain model for User
 */
data class User(
    val id: String = "1",
    val name: String = "Anderson",
    val phone: String = "+60134589525",
    val email: String = "Anderson@email.com",
    val address: String = "3 Addersion Court\nChino Hills, HO56824, United State",
    val loyaltyStamps: Int = 0,
    val maxLoyaltyStamps: Int = 8,
    val rewardPoints: Int = 0,
    val maxRewardPoints: Int = 5000
) {
    /**
     * Check if loyalty card is full
     */
    val isLoyaltyCardFull: Boolean
        get() = loyaltyStamps >= maxLoyaltyStamps

    /**
     * Loyalty stamps progress (0.0 to 1.0)
     */
    val loyaltyProgress: Float
        get() = if (maxLoyaltyStamps > 0) {
            (loyaltyStamps.toFloat() / maxLoyaltyStamps).coerceIn(0f, 1f)
        } else 0f

    /**
     * Loyalty stamps display string (e.g., "4/8")
     */
    val loyaltyStampsDisplay: String
        get() = "$loyaltyStamps/$maxLoyaltyStamps"

    /**
     * Reward points progress (0.0 to 1.0)
     */
    val rewardPointsProgress: Float
        get() = if (maxRewardPoints > 0) {
            (rewardPoints.toFloat() / maxRewardPoints).coerceIn(0f, 1f)
        } else 0f

    /**
     * Reward points display string (e.g., "2750/5000")
     */
    val rewardPointsDisplay: String
        get() = "$rewardPoints/$maxRewardPoints"

    /**
     * Formatted reward points (e.g., "2,750")
     */
    val formattedRewardPoints: String
        get() = String.format(Locale.US, "%,d", rewardPoints)

    /**
     * Add a stamp (returns new User with updated stamps)
     */
    fun addLoyaltyStamp(): User {
        val newStamps = if (loyaltyStamps >= maxLoyaltyStamps) 0 else loyaltyStamps + 1
        return copy(loyaltyStamps = newStamps)
    }

    /**
     * Reset loyalty stamps
     */
    fun resetLoyaltyStamps(): User = copy(loyaltyStamps = 0)

    /**
     * Add reward points
     */
    fun addRewardPoints(points: Int): User = copy(rewardPoints = rewardPoints + points)

    /**
     * Use reward points (returns null if not enough points)
     */
    fun useRewardPoints(points: Int): User? {
        return if (rewardPoints >= points) {
            copy(rewardPoints = rewardPoints - points)
        } else {
            null
        }
    }
}

