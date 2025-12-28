package com.example.thecodecup.domain.model

/**
 * Domain model for redeemable Reward
 */
data class Reward(
    val id: Int,
    val coffeeName: String,
    val validUntil: String,
    val pointsRequired: Int,
    val isRedeemed: Boolean = false
)

/**
 * Domain model for Reward History entry
 */
data class RewardHistory(
    val id: Int,
    val coffeeName: String,
    val points: Int,
    val date: String,
    val type: RewardType = RewardType.EARNED
)

/**
 * Type of reward transaction
 */
enum class RewardType {
    EARNED,    // Points earned from purchase
    REDEEMED   // Points used for redemption
}

