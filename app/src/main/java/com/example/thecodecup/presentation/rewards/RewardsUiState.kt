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
    val rewardPoints: Int = 0,
    val maxRewardPoints: Int = 5000,
    val rewardPointsProgress: Float = 0f,
    val rewardPointsDisplay: String = "0/5000",
    val formattedRewardPoints: String = "0",
    val historyItems: List<RewardHistory> = emptyList(),
    val availableRewards: List<Reward> = emptyList(),
    val isLoading: Boolean = false,
    val isRedeeming: Boolean = false,
    val redeemSuccess: Boolean = false,
    val redeemStampsSuccess: Boolean = false,
    val redeemError: String? = null,
    val errorMessage: String? = null
) {
    /**
     * Check if user can redeem a free coffee (100 points required)
     */
    val canRedeemFreeCoffee: Boolean
        get() = rewardPoints >= FREE_COFFEE_POINTS_REQUIRED

    /**
     * Check if user can redeem loyalty stamps (8 stamps required)
     */
    val canRedeemStamps: Boolean
        get() = loyaltyStamps >= maxLoyaltyStamps

    companion object {
        const val FREE_COFFEE_POINTS_REQUIRED = 100
    }
}

/**
 * UI State for Redeem Screen
 */
data class RedeemUiState(
    val currentPoints: Int = 2750,
    val formattedPoints: String = "2,750",
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
    data object RedeemFreeCoffee : RewardsUiEvent
    data object RedeemStamps : RewardsUiEvent
    data object ConsumeRedeemSuccess : RewardsUiEvent
    data object ConsumeRedeemStampsSuccess : RewardsUiEvent
    data object ConsumeRedeemError : RewardsUiEvent
}

/**
 * Events from Redeem Screen
 */
sealed interface RedeemUiEvent {
    data class RedeemReward(val rewardId: Int) : RedeemUiEvent
    data object ClearError : RedeemUiEvent
    data object NavigateBack : RedeemUiEvent
}
