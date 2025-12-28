package com.example.thecodecup.presentation.rewards

import com.example.thecodecup.domain.model.Reward
import com.example.thecodecup.domain.model.RewardHistory

/**
 * UI State for Rewards Screen
 */
data class RewardsUiState(
    val loyaltyStamps: Int = 0,
    val maxLoyaltyStamps: Int = 8,
    val loyaltyProgress: Float = 0f,
    val loyaltyStampsDisplay: String = "0/8",
    val rewardPoints: Int = 50,  // Default 50 points for demo
    val maxRewardPoints: Int = 100,  // Max 100 points
    val rewardPointsProgress: Float = 0.5f,
    val rewardPointsDisplay: String = "50/100",
    val formattedRewardPoints: String = "50",
    val voucherCount: Int = 0,  // Number of 2,000 VND vouchers
    val historyItems: List<RewardHistory> = emptyList(),
    val availableRewards: List<Reward> = emptyList(),
    val selectedReward: Reward? = null,  // Currently selected reward for redemption
    val showRedeemDialog: Boolean = false,  // Show confirmation dialog
    val isLoading: Boolean = false,
    val isRedeeming: Boolean = false,
    val redeemSuccess: Boolean = false,
    val redeemStampsSuccess: Boolean = false,
    val redeemStampsMessage: String? = null,  // Custom message for stamp redemption
    val redeemError: String? = null,
    val redeemedRewardName: String? = null,  // Name of successfully redeemed reward
    val errorMessage: String? = null
) {
    /**
     * Check if user can redeem a specific reward
     */
    fun canRedeemReward(reward: Reward): Boolean = rewardPoints >= reward.pointsRequired

    /**
     * Check if user can redeem the selected reward
     */
    val canRedeemSelectedReward: Boolean
        get() = selectedReward?.let { rewardPoints >= it.pointsRequired } ?: false

    /**
     * Check if user can redeem loyalty stamps (8 stamps required)
     */
    val canRedeemStamps: Boolean
        get() = loyaltyStamps >= maxLoyaltyStamps

    /**
     * Get rewards that user can afford
     */
    val affordableRewards: List<Reward>
        get() = availableRewards.filter { rewardPoints >= it.pointsRequired }

    companion object {
        // Minimum points thresholds for different reward tiers
        const val TIER_1_POINTS = 30   // Small treats
        const val TIER_2_POINTS = 50   // Regular drinks
        const val TIER_3_POINTS = 80   // Premium drinks
        const val TIER_4_POINTS = 120  // Combo/Special
    }
}

/**
 * UI State for Redeem Screen
 */
data class RedeemUiState(
    val currentPoints: Int = 50,
    val formattedPoints: String = "50",
    val availableRewards: List<Reward> = emptyList(),
    val isLoading: Boolean = false,
    val isRedeeming: Boolean = false,
    val redeemSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Events from Rewards Screen
 */
sealed interface RewardsUiEvent {
    data object LoadData : RewardsUiEvent
    data object ClearError : RewardsUiEvent
    data object LoyaltyCardClicked : RewardsUiEvent
    data object RedeemClicked : RewardsUiEvent
    data object RedeemStamps : RewardsUiEvent
    data object ConsumeRedeemSuccess : RewardsUiEvent
    data object ConsumeRedeemStampsSuccess : RewardsUiEvent
    data object ConsumeRedeemError : RewardsUiEvent

    // New events for reward-based redemption
    data class SelectRewardToRedeem(val reward: Reward) : RewardsUiEvent
    data object ConfirmRedemption : RewardsUiEvent
    data object CancelRedemption : RewardsUiEvent
    data class RedeemSpecificReward(val reward: Reward) : RewardsUiEvent
}

/**
 * Events from Redeem Screen
 */
sealed interface RedeemUiEvent {
    data class RedeemReward(val rewardId: Int) : RedeemUiEvent
    data object ClearError : RedeemUiEvent
    data object NavigateBack : RedeemUiEvent
}
