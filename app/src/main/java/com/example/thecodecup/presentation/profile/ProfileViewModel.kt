package com.example.thecodecup.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.di.ServiceLocator
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.repository.OrderRepository
import com.example.thecodecup.domain.usecase.user.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecase.user.UpdateUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Profile Screen
 * Uses UseCases instead of Repositories directly
 */
class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase(),
    private val updateUserProfileUseCase: UpdateUserProfileUseCase = ServiceLocator.provideUpdateUserProfileUseCase(),
    private val orderRepository: OrderRepository = ServiceLocator.provideOrderRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
        loadOrderHistory()
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.StartEditing -> startEditing(event.field)
            is ProfileUiEvent.UpdateEditValue -> updateEditValue(event.value)
            is ProfileUiEvent.SaveEdit -> saveEdit()
            is ProfileUiEvent.CancelEdit -> cancelEdit()
            is ProfileUiEvent.ConsumeUpdateSuccess -> consumeUpdateSuccess()
            is ProfileUiEvent.ClearError -> clearError()
            is ProfileUiEvent.NavigateBack -> { /* Handled by screen */ }
            is ProfileUiEvent.LogOut -> handleLogOut()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        val user = result.data
                        val initials = user.name.split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            .take(2)
                            .joinToString("")
                            .ifEmpty { "U" }

                        _uiState.update { state ->
                            state.copy(
                                fullName = user.name,
                                phoneNumber = user.phone,
                                email = user.email,
                                address = user.address,
                                avatarInitials = initials,
                                rewardPoints = user.rewardPoints
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

    private fun loadOrderHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingOrders = true) }

            try {
                orderRepository.getOrderHistory().collect { orders ->
                    val totalSpent = orders.sumOf { it.totalPrice }
                    val loyaltyTier = LoyaltyTier.fromTotalSpent(totalSpent)

                    _uiState.update { state ->
                        state.copy(
                            orderHistory = orders,
                            totalSpent = totalSpent,
                            loyaltyTier = loyaltyTier,
                            isLoadingOrders = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoadingOrders = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun handleLogOut() {
        // Mock logout - in a real app, this would clear session, tokens, etc.
        viewModelScope.launch {
            // For now, just show a message or handle navigation
            _uiState.update { state ->
                state.copy(errorMessage = "Logout functionality - placeholder")
            }
        }
    }

    private fun startEditing(field: ProfileField) {
        val currentValue = when (field) {
            ProfileField.FULL_NAME -> _uiState.value.fullName
            ProfileField.PHONE_NUMBER -> _uiState.value.phoneNumber
            ProfileField.EMAIL -> _uiState.value.email
            ProfileField.ADDRESS -> _uiState.value.address
        }
        _uiState.update {
            it.copy(
                editingField = field,
                editValue = currentValue
            )
        }
    }

    private fun updateEditValue(value: String) {
        _uiState.update { it.copy(editValue = value) }
    }

    private fun saveEdit() {
        val currentState = _uiState.value
        val field = currentState.editingField ?: return
        val newValue = currentState.editValue

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            // Build params based on field being edited
            val params = when (field) {
                ProfileField.FULL_NAME -> UpdateUserProfileUseCase.Params(name = newValue)
                ProfileField.PHONE_NUMBER -> UpdateUserProfileUseCase.Params(phone = newValue)
                ProfileField.EMAIL -> UpdateUserProfileUseCase.Params(email = newValue)
                ProfileField.ADDRESS -> UpdateUserProfileUseCase.Params(address = newValue)
            }

            when (val result = updateUserProfileUseCase(params)) {
                is DomainResult.Success -> {
                    _uiState.update {
                        it.copy(
                            editingField = null,
                            editValue = "",
                            isSaving = false,
                            isUpdateSuccess = true
                        )
                    }
                }
                is DomainResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.exception.message
                        )
                    }
                }
            }
        }
    }

    private fun cancelEdit() {
        _uiState.update {
            it.copy(
                editingField = null,
                editValue = ""
            )
        }
    }

    private fun consumeUpdateSuccess() {
        _uiState.update { it.copy(isUpdateSuccess = false) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
