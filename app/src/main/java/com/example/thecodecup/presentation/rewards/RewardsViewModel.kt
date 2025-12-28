package com.example.thecodecup.presentation.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.di.ServiceLocator
import com.example.thecodecup.domain.common.DomainResult
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
 * Uses UseCases instead of Repositories directly
 */
class RewardsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userRepository: UserRepository = ServiceLocator.provideUserRepository(),
    private val rewardRepository: RewardRepository = ServiceLocator.provideRewardRepository()
) : ViewModel() {

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
            is RewardsUiEvent.RedeemFreeCoffee -> redeemFreeCoffee()
            is RewardsUiEvent.RedeemStamps -> redeemStamps()
            is RewardsUiEvent.ConsumeRedeemSuccess -> consumeRedeemSuccess()
            is RewardsUiEvent.ConsumeRedeemStampsSuccess -> consumeRedeemStampsSuccess()
            is RewardsUiEvent.ConsumeRedeemError -> consumeRedeemError()
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
                _uiState.update { state ->
                    state.copy(errorMessage = e.message)
                }
            }
        }
    }

    /**
     * Redeem a free coffee for 100 points
     */
    private fun redeemFreeCoffee() {
        viewModelScope.launch {
            val currentPoints = _uiState.value.rewardPoints
            val requiredPoints = RewardsUiState.FREE_COFFEE_POINTS_REQUIRED

            if (currentPoints < requiredPoints) {
                _uiState.update { state ->
                    state.copy(
                        redeemError = "Not enough points. You need $requiredPoints points but have $currentPoints."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isRedeeming = true) }

            try {
                // Deduct points from user
                val success = userRepository.useRewardPoints(requiredPoints)

                if (success) {
                    // Add to reward history
                    rewardRepository.addEarnedPoints("Free Coffee (Redeemed)", -requiredPoints)

                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            redeemSuccess = true,
                            rewardPoints = state.rewardPoints - requiredPoints
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            redeemError = "Not enough points"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isRedeeming = false,
                        redeemError = e.message ?: "Failed to redeem"
                    )
                }
            }
        }
    }

    /**
     * Redeem loyalty stamps when user has 8 stamps
     */
    private fun redeemStamps() {
        viewModelScope.launch {
            val currentStamps = _uiState.value.loyaltyStamps
            val maxStamps = _uiState.value.maxLoyaltyStamps

            if (currentStamps < maxStamps) {
                _uiState.update { state ->
                    state.copy(
                        redeemError = "Not enough stamps. You need $maxStamps stamps but have $currentStamps."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isRedeeming = true) }

            try {
                // Reset stamps to 0
                userRepository.resetLoyaltyStamps()

                // Add to reward history
                rewardRepository.addEarnedPoints("Free Coffee (Loyalty Card)", 0)

                _uiState.update { state ->
                    state.copy(
                        isRedeeming = false,
                        redeemStampsSuccess = true,
                        loyaltyStamps = 0,
                        loyaltyProgress = 0f,
                        loyaltyStampsDisplay = "0/$maxStamps"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isRedeeming = false,
                        redeemError = e.message ?: "Failed to redeem"
                    )
                }
            }
        }
    }

    private fun consumeRedeemStampsSuccess() {
        _uiState.update { it.copy(redeemStampsSuccess = false) }
    }

    private fun consumeRedeemSuccess() {
        _uiState.update { it.copy(redeemSuccess = false) }
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
                val success = rewardRepository.redeemReward(rewardId)
                if (success) {
                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            redeemSuccess = true
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isRedeeming = false,
                            errorMessage = "Not enough points to redeem this reward"
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
