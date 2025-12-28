package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.model.Reward
import com.example.thecodecup.domain.model.RewardHistory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Reward operations
 */
interface RewardRepository {

    /**
     * Observe available rewards
     */
    fun observeAvailableRewards(): Flow<List<Reward>>

    /**
     * Observe reward history
     */
    fun observeRewardHistory(): Flow<List<RewardHistory>>

    /**
     * Get available rewards
     */
    suspend fun getAvailableRewards(): List<Reward>

    /**
     * Get reward history
     */
    suspend fun getRewardHistory(): List<RewardHistory>

    /**
     * Redeem a reward (returns false if not enough points)
     */
    suspend fun redeemReward(rewardId: Int): Boolean

    /**
     * Add earned points from purchase
     */
    suspend fun addEarnedPoints(coffeeName: String, points: Int)
}

