package com.example.thecodecup.data.repository

import com.example.thecodecup.domain.model.User
import com.example.thecodecup.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Implementation of UserRepository
 * Uses in-memory storage with StateFlow for reactivity
 */
class UserRepositoryImpl : UserRepository {

    private val _user = MutableStateFlow(User())

    override fun observeUser(): Flow<User> = _user.asStateFlow()

    override suspend fun getUser(): User = _user.value

    override suspend fun updateUser(user: User) {
        _user.value = user
    }

    override suspend fun updateName(name: String) {
        _user.update { it.copy(name = name) }
    }

    override suspend fun updatePhone(phone: String) {
        _user.update { it.copy(phone = phone) }
    }

    override suspend fun updateEmail(email: String) {
        _user.update { it.copy(email = email) }
    }

    override suspend fun updateAddress(address: String) {
        _user.update { it.copy(address = address) }
    }

    override suspend fun addLoyaltyStamp() {
        _user.update { user ->
            if (user.loyaltyStamps >= user.maxLoyaltyStamps) {
                user.copy(loyaltyStamps = 1) // Reset and add one
            } else {
                user.copy(loyaltyStamps = user.loyaltyStamps + 1)
            }
        }
    }

    override suspend fun resetLoyaltyStamps() {
        _user.update { it.copy(loyaltyStamps = 0) }
    }

    override suspend fun addRewardPoints(points: Int) {
        _user.update { it.copy(rewardPoints = it.rewardPoints + points) }
    }

    override suspend fun useRewardPoints(points: Int): Boolean {
        val currentPoints = _user.value.rewardPoints
        return if (currentPoints >= points) {
            _user.update { it.copy(rewardPoints = it.rewardPoints - points) }
            true
        } else {
            false
        }
    }

    override suspend fun addVoucher() {
        _user.update { it.copy(voucherCount = it.voucherCount + 1) }
    }

    override suspend fun useVoucher(): Boolean {
        val currentVouchers = _user.value.voucherCount
        return if (currentVouchers > 0) {
            _user.update { it.copy(voucherCount = it.voucherCount - 1) }
            true
        } else {
            false
        }
    }

    override suspend fun getVoucherCount(): Int {
        return _user.value.voucherCount
    }
}

