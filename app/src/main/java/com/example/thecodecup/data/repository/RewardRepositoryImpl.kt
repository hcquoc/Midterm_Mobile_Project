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
            // Đồ uống có thể đổi với 50 điểm (món từ 50,000 VND trở xuống)
            Reward(1, "Espresso Shot", "Không giới hạn", 50),        // 35,000 VND
            Reward(2, "Americano", "Không giới hạn", 50),             // 40,000 VND
            Reward(3, "Cà phê Đen", "Không giới hạn", 50),            // 29,000 VND
            Reward(4, "Cà phê Sữa", "Không giới hạn", 50),            // 35,000 VND
            Reward(5, "Bạc Sỉu", "Không giới hạn", 50),               // 32,000 VND
            Reward(6, "Cappuccino", "Không giới hạn", 50),            // 45,000 VND
            Reward(7, "Cafe Latte", "Không giới hạn", 50),            // 50,000 VND
            Reward(8, "Trà Sen", "Không giới hạn", 50),               // 35,000 VND
            Reward(9, "Trà Đào", "Không giới hạn", 50),               // 40,000 VND
            // Bánh ngọt có thể đổi với 50 điểm (món từ 50,000 VND trở xuống)
            Reward(10, "Mousse", "Không giới hạn", 50),               // 45,000 VND
            Reward(11, "Cupcake", "Không giới hạn", 50),              // 35,000 VND
            Reward(12, "Pudding", "Không giới hạn", 50)               // 40,000 VND
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
        // Only add points if positive (earned points)
        // Negative points are just for history display (redemption already deducted)
        if (points > 0) {
            userRepository.addRewardPoints(points)
        }

        // Add to history
        val dateFormat = SimpleDateFormat("dd MMMM | hh:mm a", Locale.US)
        val historyEntry = RewardHistory(
            id = nextHistoryId++,
            coffeeName = coffeeName,
            points = points,
            date = dateFormat.format(Date()),
            type = if (points >= 0) RewardType.EARNED else RewardType.REDEEMED
        )

        _rewardHistory.update { history -> listOf(historyEntry) + history }
    }
}

