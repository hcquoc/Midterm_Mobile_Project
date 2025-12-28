package com.example.thecodecup.presentation.home

import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeCategory

/**
 * UI State for Home Screen
 */
data class HomeUiState(
    val userName: String = "Anderson",
    val greeting: String = "Good morning",
    val loyaltyStamps: Int = 4,
    val maxLoyaltyStamps: Int = 8,
    val loyaltyProgress: Float = 0.5f,
    val loyaltyStampsDisplay: String = "4/8",
    val rewardPoints: Int = 0,
    val coffeeList: List<Coffee> = emptyList(),
    val filteredCoffeeList: List<Coffee> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: CoffeeCategory = CoffeeCategory.ALL,
    val categories: List<CoffeeCategory> = CoffeeCategory.entries,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigateToCart: Boolean = false,
    val addToCartSuccess: Boolean = false
)

/**
 * Events from Home Screen
 */
sealed interface HomeUiEvent {
    data object LoadData : HomeUiEvent
    data object ClearError : HomeUiEvent
    data class SearchQueryChanged(val query: String) : HomeUiEvent
    data class CategoryChanged(val category: CoffeeCategory) : HomeUiEvent
    data class AddToCartAndNavigate(val coffee: Coffee) : HomeUiEvent
    data object NavigateToCartHandled : HomeUiEvent
    data class CoffeeClicked(val coffee: Coffee) : HomeUiEvent
    data object CartClicked : HomeUiEvent
    data object ProfileClicked : HomeUiEvent
    data object RewardsClicked : HomeUiEvent
    data object OrdersClicked : HomeUiEvent
}
