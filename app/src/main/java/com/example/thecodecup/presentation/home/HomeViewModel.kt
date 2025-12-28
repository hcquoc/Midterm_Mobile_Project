package com.example.thecodecup.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.usecase.coffee.GetCoffeeMenuUseCase
import com.example.thecodecup.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for Home Screen
 * Uses UseCases instead of Repositories directly
 */
class HomeViewModel(
    private val getCoffeeMenuUseCase: GetCoffeeMenuUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "HomeViewModel init started")
        try {
            loadCoffeeMenu()
            loadUserData()
            Log.d("HomeViewModel", "HomeViewModel init completed")
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error in HomeViewModel init", e)
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.LoadData -> {
                loadCoffeeMenu()
                loadUserData()
            }
            is HomeUiEvent.ClearError -> clearError()
            else -> { /* Navigation events handled by screen */ }
        }
    }

    private fun loadCoffeeMenu() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "loadCoffeeMenu started")
            _uiState.update { it.copy(isLoading = true) }

            getCoffeeMenuUseCase().collect { result ->
                Log.d("HomeViewModel", "getCoffeeMenuUseCase result: $result")
                when (result) {
                    is DomainResult.Success -> {
                        Log.d("HomeViewModel", "Loaded ${result.data.size} coffees")
                        _uiState.update { state ->
                            state.copy(
                                coffeeList = result.data,
                                isLoading = false,
                                greeting = getGreeting()
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        Log.e("HomeViewModel", "Error loading coffees: ${result.exception.message}")
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = result.exception.message
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        val user = result.data
                        _uiState.update { state ->
                            state.copy(
                                userName = user.name,
                                loyaltyStamps = user.loyaltyStamps,
                                maxLoyaltyStamps = user.maxLoyaltyStamps,
                                loyaltyProgress = user.loyaltyProgress,
                                loyaltyStampsDisplay = user.loyaltyStampsDisplay,
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

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}
