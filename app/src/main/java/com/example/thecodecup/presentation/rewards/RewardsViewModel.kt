package com.example.thecodecup.presentation.rewards

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.di.ServiceLocator
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Reward
import com.example.thecodecup.domain.repository.RewardRepository
import com.example.thecodecup.domain.repository.UserRepository
import com.example.thecodecup.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Rewards Screen
 * Handles loyalty points, stamps, and reward redemption
 */
class RewardsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userRepository: UserRepository = ServiceLocator.provideUserRepository(),
    private val rewardRepository: RewardRepository = ServiceLocator.provideRewardRepository()
) : ViewModel() {

    companion object {
        private const val TAG = "RewardsViewModel"
    }

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: RewardsUiEvent) {
        when (event) {
            is RewardsUiEvent.LoadData -> loadData()
            is RewardsUiEvent.ClearError -> clearError()
            is RewardsUiEvent.LoyaltyCardClicked -> { /* Handled by screen */ }
            is RewardsUiEvent.RedeemClicked -> { /* Handled by screen */ }
            is RewardsUiEvent.RedeemStamps -> redeemStamps()
            is RewardsUiEvent.ConsumeRedeemSuccess -> consumeRedeemSuccess()
            is RewardsUiEvent.ConsumeRedeemStampsSuccess -> consumeRedeemStampsSuccess()
            is RewardsUiEvent.ConsumeRedeemError -> consumeRedeemError()

            // New reward-based redemption events
            is RewardsUiEvent.SelectRewardToRedeem -> selectRewardToRedeem(event.reward)
            is RewardsUiEvent.ConfirmRedemption -> confirmRedemption()
            is RewardsUiEvent.CancelRedemption -> cancelRedemption()
            is RewardsUiEvent.RedeemSpecificReward -> redeemSpecificReward(event.reward)
        }
    }

    private fun loadData() {
        loadUserData()
        loadRewardHistory()
        loadAvailableRewards()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        val user = result.data
                        _uiState.update { state ->
                            state.copy(
                                loyaltyStamps = user.loyaltyStamps,
                                maxLoyaltyStamps = user.maxLoyaltyStamps,
                                loyaltyProgress = user.loyaltyProgress,
                                loyaltyStampsDisplay = user.loyaltyStampsDisplay,
                                rewardPoints = user.rewardPoints,
                                maxRewardPoints = user.maxRewardPoints,
                                rewardPointsProgress = user.rewardPointsProgress,
                                rewardPointsDisplay = user.rewardPointsDisplay,
                                formattedRewardPoints = user.formattedRewardPoints,
                                voucherCount = user.voucherCount,
                                isLoading = false
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        _uiState.update { state ->
                            state.copy(
                                errorMessage = result.exception.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadRewardHistory() {
        viewModelScope.launch {
            try {
                rewardRepository.observeRewardHistory().collect { history ->
                    _uiState.update { state ->
                        state.copy(historyItems = history)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading reward history", e)
                _uiState.update { state ->
                    state.copy(errorMessage = e.message)
                }
            }
        }
    }

    private fun loadAvailableRewards() {
        viewModelScope.launch {
            try {
                rewardRepository.observeAvailableRewards().collect { rewards ->
                    _uiState.update { state ->
                        state.copy(availableRewards = rewards)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading available rewards", e)
                _uiState.update { state ->
                    state.copy(errorMessage = e.message)
                }
            }
        }
    }

    /**
     * Select a reward to redeem - shows confirmation dialog
     */
    private fun selectRewardToRedeem(reward: Reward) {
        val currentPoints = _uiState.value.rewardPoints

        if (currentPoints < reward.pointsRequired) {
            _uiState.update { state ->
                state.copy(
                    redeemError = "Không đủ điểm. Bạn cần ${reward.pointsRequired} điểm nhưng chỉ có $currentPoints điểm."
                )
            }
            return
        }

        _uiState.update { state ->
            state.copy(
                selectedReward = reward,
                showRedeemDialog = true
            )
        }
    }

    /**
     * Cancel the redemption dialog
     */
    private fun cancelRedemption() {
        _uiState.update { state ->
            state.copy(
                selectedReward = null,
                showRedeemDialog = false
            )
        }
    }

    /**
     * Confirm and execute the redemption for selected reward
     */
    private fun confirmRedemption() {
        val selectedReward = _uiState.value.selectedReward ?: return
        redeemSpecificReward(selectedReward)
    }

    /**
     * Redeem a specific reward by deducting its required points
     */
    private fun redeemSpecificReward(reward: Reward) {
        viewModelScope.launch {
            val currentPoints = _uiState.value.rewardPoints
            val requiredPoints = reward.pointsRequired

            if (currentPoints < requiredPoints) {
                _uiState.update { state ->
                    state.copy(
                        showRedeemDialog = false,
                        selectedReward = null,
                        redeemError = "Không đủ điểm. Bạn cần $requiredPoints điểm nhưng chỉ có $currentPoints điểm."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isRedeeming = true) }

            try {
                // Deduct points from user
                val success = userRepository.useRewardPoints(requiredPoints)

                if (success) {
                    // Add to reward history with negative points (redeemed)
                    rewardRepository.addEarnedPoints("${reward.coffeeName} (Đã đổi)", -requiredPoints)

                    Log.d(TAG, "Successfully redeemed: ${reward.coffeeName} for $requiredPoints points")

                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            showRedeemDialog = false,
                            selectedReward = null,
                            redeemSuccess = true,
                            redeemedRewardName = reward.coffeeName,
                            rewardPoints = state.rewardPoints - requiredPoints,
                            rewardPointsDisplay = "${state.rewardPoints - requiredPoints}/${state.maxRewardPoints}",
                            formattedRewardPoints = (state.rewardPoints - requiredPoints).toString()
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            showRedeemDialog = false,
                            selectedReward = null,
                            redeemError = "Không đủ điểm để đổi thưởng"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error redeeming reward", e)
                _uiState.update { state ->
                    state.copy(
                        isRedeeming = false,
                        showRedeemDialog = false,
                        selectedReward = null,
                        redeemError = e.message ?: "Không thể đổi thưởng"
                    )
                }
            }
        }
    }

    /**
     * Redeem loyalty stamps when user has 8 stamps
     * Gives user a 2,000 VND voucher
     */
    private fun redeemStamps() {
        viewModelScope.launch {
            val currentStamps = _uiState.value.loyaltyStamps
            val maxStamps = _uiState.value.maxLoyaltyStamps

            if (currentStamps < maxStamps) {
                _uiState.update { state ->
                    state.copy(
                        redeemError = "Chưa đủ tem. Bạn cần $maxStamps tem nhưng chỉ có $currentStamps tem."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isRedeeming = true) }

            try {
                // Reset stamps to 0
                userRepository.resetLoyaltyStamps()

                // Add a voucher (worth 2,000 VND)
                userRepository.addVoucher()

                // Get new voucher count
                val newVoucherCount = userRepository.getVoucherCount()

                // Add to reward history
                rewardRepository.addEarnedPoints("Voucher 2K (Đổi 8 tem)", 0)

                Log.d(TAG, "Successfully redeemed loyalty stamps for a voucher. Total vouchers: $newVoucherCount")

                _uiState.update { state ->
                    state.copy(
                        isRedeeming = false,
                        redeemStampsSuccess = true,
                        redeemStampsMessage = "Bạn nhận được 1 Voucher 2K! Tổng voucher: $newVoucherCount",
                        loyaltyStamps = 0,
                        loyaltyProgress = 0f,
                        loyaltyStampsDisplay = "0/$maxStamps",
                        voucherCount = newVoucherCount
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error redeeming stamps", e)
                _uiState.update { state ->
                    state.copy(
                        isRedeeming = false,
                        redeemError = e.message ?: "Không thể đổi tem"
                    )
                }
            }
        }
    }

    private fun consumeRedeemStampsSuccess() {
        _uiState.update { it.copy(redeemStampsSuccess = false, redeemStampsMessage = null) }
    }

    private fun consumeRedeemSuccess() {
        _uiState.update { it.copy(redeemSuccess = false, redeemedRewardName = null) }
    }

    private fun consumeRedeemError() {
        _uiState.update { it.copy(redeemError = null) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

/**
 * ViewModel for Redeem Screen
 * Uses GetCurrentUserUseCase for user points
 */
class RedeemViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase(),
    private val userRepository: UserRepository = ServiceLocator.provideUserRepository(),
    private val rewardRepository: RewardRepository = ServiceLocator.provideRewardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RedeemUiState())
    val uiState: StateFlow<RedeemUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: RedeemUiEvent) {
        when (event) {
            is RedeemUiEvent.RedeemReward -> redeemReward(event.rewardId)
            is RedeemUiEvent.ClearError -> clearError()
            is RedeemUiEvent.NavigateBack -> { /* Handled by screen */ }
        }
    }

    private fun loadData() {
        loadUserPoints()
        loadAvailableRewards()
    }

    private fun loadUserPoints() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        val user = result.data
                        _uiState.update { state ->
                            state.copy(
                                currentPoints = user.rewardPoints,
                                formattedPoints = user.formattedRewardPoints
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        _uiState.update { state ->
                            state.copy(errorMessage = result.exception.message)
                        }
                    }
                }
            }
        }
    }

    private fun loadAvailableRewards() {
        viewModelScope.launch {
            try {
                rewardRepository.observeAvailableRewards().collect { rewards ->
                    _uiState.update { state ->
                        state.copy(
                            availableRewards = rewards,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun redeemReward(rewardId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRedeeming = true) }

            try {
                val reward = _uiState.value.availableRewards.find { it.id == rewardId }
                if (reward == null) {
                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            errorMessage = "Không tìm thấy phần thưởng"
                        )
                    }
                    return@launch
                }

                val currentPoints = _uiState.value.currentPoints
                if (currentPoints < reward.pointsRequired) {
                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            errorMessage = "Không đủ điểm. Cần ${reward.pointsRequired} điểm."
                        )
                    }
                    return@launch
                }

                // Deduct points
                val success = userRepository.useRewardPoints(reward.pointsRequired)
                if (success) {
                    // Mark reward as redeemed and add to history
                    rewardRepository.redeemReward(rewardId)
                    rewardRepository.addEarnedPoints("${reward.coffeeName} (Đã đổi)", -reward.pointsRequired)

                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            redeemSuccess = true,
                            currentPoints = state.currentPoints - reward.pointsRequired
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            errorMessage = "Không đủ điểm để đổi thưởng này"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isRedeeming = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun consumeRedeemSuccess() {
        _uiState.update { it.copy(redeemSuccess = false) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
