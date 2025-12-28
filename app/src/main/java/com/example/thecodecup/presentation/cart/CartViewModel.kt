package com.example.thecodecup.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.thecodecup.domain.usecase.cart.GetCartItemsUseCase
import com.example.thecodecup.domain.usecase.cart.RemoveFromCartUseCase
import com.example.thecodecup.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.example.thecodecup.domain.usecase.order.PlaceOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/**
 * ViewModel for Cart Screen
 * Uses UseCases instead of Repositories directly
 */
class CartViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    /**
     * Exposes the cart item count as a StateFlow for use in Navigation badges
     */
    val cartItemCount: StateFlow<Int> = _uiState
        .map { it.cart.itemCount }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        observeCart()
    }

    fun onEvent(event: CartUiEvent) {
        when (event) {
            is CartUiEvent.LoadCart -> observeCart()
            is CartUiEvent.RemoveItem -> removeItem(event.itemId)
            is CartUiEvent.UpdateQuantity -> updateQuantity(event.itemId, event.quantity)
            is CartUiEvent.PlaceOrder -> placeOrder(event.note, event.address)
            is CartUiEvent.ConsumeOrderSuccess -> consumeOrderSuccess()
            is CartUiEvent.ClearError -> clearError()
            is CartUiEvent.NavigateBack -> { /* Handled by screen */ }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getCartItemsUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        val cart = result.data
                        _uiState.update { state ->
                            state.copy(
                                cart = cart,
                                isLoading = false
                            )
                        }
                        // Also calculate total
                        calculateTotal()
                    }
                    is DomainResult.Error -> {
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

    private fun calculateTotal() {
        viewModelScope.launch {
            when (val result = calculateCartTotalUseCase()) {
                is DomainResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            subtotal = result.data.subtotal,
                            deliveryFee = result.data.deliveryFee,
                            totalPrice = result.data.total
                        )
                    }
                }
                is DomainResult.Error -> {
                    // Ignore calculation errors, cart will show raw total
                }
            }
        }
    }

    private fun removeItem(itemId: Int) {
        viewModelScope.launch {
            when (val result = removeFromCartUseCase(itemId)) {
                is DomainResult.Success -> {
                    // Cart will be updated via Flow
                }
                is DomainResult.Error -> {
                    _uiState.update { state ->
                        state.copy(errorMessage = result.exception.message)
                    }
                }
            }
        }
    }

    private fun updateQuantity(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            when (val result = updateCartItemQuantityUseCase(itemId, quantity)) {
                is DomainResult.Success -> {
                    // Cart will be updated via Flow
                }
                is DomainResult.Error -> {
                    _uiState.update { state ->
                        state.copy(errorMessage = result.exception.message)
                    }
                }
            }
        }
    }

    private fun placeOrder(note: String?, address: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingOut = true) }

            when (val result = placeOrderUseCase(note = note, deliveryAddress = address)) {
                is DomainResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isCheckingOut = false,
                            orderSuccess = true,
                            orderId = result.data.order.id,
                            pointsEarned = result.data.pointsEarned
                        )
                    }
                }
                is DomainResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isCheckingOut = false,
                            errorMessage = result.exception.message
                        )
                    }
                }
            }
        }
    }

    private fun consumeOrderSuccess() {
        _uiState.update { it.copy(orderSuccess = false, orderId = null, pointsEarned = 0) }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
