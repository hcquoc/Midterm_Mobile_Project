package com.example.thecodecup.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeCategory
import com.example.thecodecup.domain.model.CoffeeOptions
import com.example.thecodecup.domain.usecase.cart.AddToCartUseCase
import com.example.thecodecup.domain.usecase.coffee.GetCoffeeMenuUseCase
import com.example.thecodecup.domain.usecase.user.GetCurrentUserUseCase
import com.example.thecodecup.domain.model.Shot
import com.example.thecodecup.domain.model.Temperature
import com.example.thecodecup.domain.model.Size
import com.example.thecodecup.domain.model.Ice
// Hoặc import gộp: import com.example.thecodecup.domain.model.*
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val addToCartUseCase: AddToCartUseCase
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
            is HomeUiEvent.SearchQueryChanged -> onSearchQueryChanged(event.query)
            is HomeUiEvent.CategoryChanged -> onCategoryChanged(event.category)
            is HomeUiEvent.AddToCartAndNavigate -> addToCartAndNavigate(event.coffee)
            is HomeUiEvent.NavigateToCartHandled -> onNavigateToCartHandled()
            else -> { /* Navigation events handled by screen */ }
        }
    }

    private fun addToCartAndNavigate(coffee: Coffee) {
        viewModelScope.launch {
            // Use default options for quick add
            val defaultOptions = CoffeeOptions(
                size = Size.MEDIUM,           // Thay "M" bằng Size.MEDIUM
                shot = Shot.SINGLE,           // Thay "Single" bằng Shot.SINGLE
                ice = Ice.FULL,               // Thay "Normal" bằng Ice.FULL (hoặc Ice.NORMAL nếu enum đó có)
                temperature = Temperature.HOT // Thay "Hot" bằng Temperature.HOT
            )

            when (val result = addToCartUseCase(coffee, defaultOptions, 1)) {
                is DomainResult.Success -> {
                    Log.d("HomeViewModel", "Added to cart: ${coffee.name}")
                    _uiState.update { it.copy(navigateToCart = true, addToCartSuccess = true) }
                }
                is DomainResult.Error -> {
                    Log.e("HomeViewModel", "Failed to add to cart: ${result.exception.message}")
                    _uiState.update { it.copy(errorMessage = result.exception.message) }
                }
            }
        }
    }

    private fun onNavigateToCartHandled() {
        _uiState.update { it.copy(navigateToCart = false, addToCartSuccess = false) }
    }

    private fun onCategoryChanged(category: CoffeeCategory) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredCoffeeList = filterCoffeeList(
                    coffeeList = state.coffeeList,
                    searchQuery = state.searchQuery,
                    category = category
                )
            )
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredCoffeeList = filterCoffeeList(
                    coffeeList = state.coffeeList,
                    searchQuery = query,
                    category = state.selectedCategory
                )
            )
        }
    }

    private fun filterCoffeeList(
        coffeeList: List<Coffee>,
        searchQuery: String,
        category: CoffeeCategory
    ): List<Coffee> {
        return coffeeList.filter { coffee ->
            val matchesSearch = searchQuery.isBlank() ||
                    coffee.name.contains(searchQuery, ignoreCase = true) ||
                    coffee.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory = category == CoffeeCategory.ALL || coffee.category == category

            matchesSearch && matchesCategory
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
                                filteredCoffeeList = result.data,
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
