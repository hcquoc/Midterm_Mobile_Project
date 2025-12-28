package com.example.thecodecup.data.repository

import com.example.thecodecup.domain.model.Reward
import com.example.thecodecup.domain.model.RewardHistory
import com.example.thecodecup.domain.model.RewardType
import com.example.thecodecup.domain.repository.RewardRepository
import com.example.thecodecup.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Implementation of RewardRepository
 * Uses in-memory storage with StateFlow for reactivity
 */
class RewardRepositoryImpl(
    private val userRepository: UserRepository
) : RewardRepository {

    private val _availableRewards = MutableStateFlow(
        listOf(
            Reward(1, "Cafe Latte", "Valid until 04.07.21", 1340),
            Reward(2, "Flat White", "Valid until 04.07.21", 1340),
            Reward(3, "Cappuccino", "Valid until 04.07.21", 1340),
            Reward(4, "Americano", "Valid until 04.07.21", 1340)
        )
    )

    private val _rewardHistory = MutableStateFlow(
        listOf(
            RewardHistory(1, "Americano", 12, "24 June | 12:30 PM", RewardType.EARNED),
            RewardHistory(2, "Cafe Latte", 12, "22 June | 08:30 AM", RewardType.EARNED),
            RewardHistory(3, "Green Tea Latte", 12, "16 June | 10:48 AM", RewardType.EARNED),
            RewardHistory(4, "Flat White", 12, "12 May | 11:25 AM", RewardType.EARNED)
        )
    )

    private var nextHistoryId = 5

    override fun observeAvailableRewards(): Flow<List<Reward>> = _availableRewards.asStateFlow()

    override fun observeRewardHistory(): Flow<List<RewardHistory>> = _rewardHistory.asStateFlow()

    override suspend fun getAvailableRewards(): List<Reward> = _availableRewards.value

    override suspend fun getRewardHistory(): List<RewardHistory> = _rewardHistory.value

    override suspend fun redeemReward(rewardId: Int): Boolean {
        val reward = _availableRewards.value.find { it.id == rewardId } ?: return false

        if (reward.isRedeemed) return false

        // Use points from user
        val success = userRepository.useRewardPoints(reward.pointsRequired)

        if (success) {
            // Mark reward as redeemed
            _availableRewards.update { rewards ->
                rewards.map { r ->
                    if (r.id == rewardId) r.copy(isRedeemed = true) else r
                }
            }

            // Add to history
            val dateFormat = SimpleDateFormat("dd MMMM | hh:mm a", Locale.US)
            val historyEntry = RewardHistory(
                id = nextHistoryId++,
                coffeeName = reward.coffeeName,
                points = -reward.pointsRequired,
                date = dateFormat.format(Date()),
                type = RewardType.REDEEMED
            )

            _rewardHistory.update { history -> listOf(historyEntry) + history }
        }

        return success
    }

    override suspend fun addEarnedPoints(coffeeName: String, points: Int) {
        // Add points to user
        userRepository.addRewardPoints(points)

        // Add to history
        val dateFormat = SimpleDateFormat("dd MMMM | hh:mm a", Locale.US)
        val historyEntry = RewardHistory(
            id = nextHistoryId++,
            coffeeName = coffeeName,
            points = points,
            date = dateFormat.format(Date()),
            type = RewardType.EARNED
        )

        _rewardHistory.update { history -> listOf(historyEntry) + history }
    }
}

