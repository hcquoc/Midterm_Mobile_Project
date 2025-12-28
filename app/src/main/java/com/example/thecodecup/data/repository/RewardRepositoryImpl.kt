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
            // Đồ uống có thể đổi với 100 điểm (100 ly = 1 phần miễn phí)
            Reward(1, "Espresso Shot", "Không giới hạn", 100),
            Reward(2, "Americano", "Không giới hạn", 100),
            Reward(3, "Cà phê Đen", "Không giới hạn", 100),
            Reward(4, "Cafe Latte", "Không giới hạn", 100),
            Reward(5, "Cappuccino", "Không giới hạn", 100),
            Reward(6, "Cà phê Sữa", "Không giới hạn", 100),
            Reward(7, "Mocha", "Không giới hạn", 100),
            Reward(8, "Caramel Macchiato", "Không giới hạn", 100),
            Reward(9, "Matcha Latte", "Không giới hạn", 100),
            // Bánh ngọt có thể đổi với 100 điểm
            Reward(10, "Tiramisu Socola", "Không giới hạn", 100),
            Reward(11, "Mousse", "Không giới hạn", 100),
            Reward(12, "Cupcake", "Không giới hạn", 100),
            Reward(13, "Pudding", "Không giới hạn", 100),
            Reward(14, "Combo Bánh Mì", "Không giới hạn", 100)
        )
    )

    private val _rewardHistory = MutableStateFlow<List<RewardHistory>>(emptyList())

    private var nextHistoryId = 1

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

