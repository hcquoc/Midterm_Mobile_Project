package com.example.thecodecup.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.CoffeeOptions
import com.example.thecodecup.domain.usecase.cart.AddToCartUseCase
import com.example.thecodecup.domain.usecase.coffee.GetCoffeeByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Detail Screen
 * Uses UseCases instead of Repositories directly
 */
class DetailViewModel(
    private val getCoffeeByIdUseCase: GetCoffeeByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadCoffee(coffeeId: Int) {
        android.util.Log.d("DetailViewModel", "loadCoffee called with id: $coffeeId")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                when (val result = getCoffeeByIdUseCase(coffeeId)) {
                    is DomainResult.Success -> {
                        android.util.Log.d("DetailViewModel", "Coffee loaded: ${result.data.name}")
                        _uiState.update {
                            it.copy(
                                coffee = result.data,
                                isLoading = false
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        android.util.Log.e("DetailViewModel", "Error loading coffee: ${result.exception.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.exception.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("DetailViewModel", "Exception in loadCoffee", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            is DetailUiEvent.SetQuantity -> {
                if (event.quantity in 1..10) {
                    _uiState.update { it.copy(quantity = event.quantity) }
                }
            }
            is DetailUiEvent.SetShot -> {
                _uiState.update { it.copy(shot = event.shot) }
            }
            is DetailUiEvent.SetTemperature -> {
                _uiState.update { it.copy(temperature = event.temperature) }
            }
            is DetailUiEvent.SetSize -> {
                _uiState.update { it.copy(size = event.size) }
            }
            is DetailUiEvent.SetIce -> {
                _uiState.update { it.copy(ice = event.ice) }
            }
            is DetailUiEvent.AddToCart -> addToCart()
            is DetailUiEvent.ConsumeAddedToCart -> consumeAddedToCart()
            is DetailUiEvent.ClearError -> clearError()
            else -> { /* Navigation events handled by screen */ }
        }
    }

    private fun addToCart() {
        val state = _uiState.value
        val coffee = state.coffee ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToCart = true) }

            val options = CoffeeOptions(
                shot = state.shot,
                temperature = state.temperature,
                size = state.size,
                ice = state.ice
            )

            when (val result = addToCartUseCase(coffee, options, state.quantity)) {
                is DomainResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isAddingToCart = false,
                            addedToCart = true
                        )
                    }
                }
                is DomainResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isAddingToCart = false,
                            errorMessage = result.exception.message
                        )
                    }
                }
            }
        }
    }

    private fun consumeAddedToCart() {
        _uiState.update { it.copy(addedToCart = false) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
