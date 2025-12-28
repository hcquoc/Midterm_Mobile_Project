package com.example.thecodecup.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.thecodecup.domain.usecase.cart.GetCartItemsUseCase
import com.example.thecodecup.domain.usecase.cart.RemoveFromCartUseCase
import com.example.thecodecup.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.example.thecodecup.domain.usecase.order.PlaceOrderUseCase
import com.example.thecodecup.domain.usecase.recommendation.GetRecommendationsUseCase
import com.example.thecodecup.domain.usecase.user.GetCurrentUserUseCase
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
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
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
        observeUser()
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
            is CartUiEvent.ToggleUsePoints -> toggleUsePoints(event.usePoints)
        }
    }

    /**
     * Observe user data for points information
     */
    private fun observeUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        _uiState.update { state ->
                            state.copy(user = result.data)
                        }
                        // Recalculate discount if usePoints is enabled
                        if (_uiState.value.usePoints) {
                            calculatePointsDiscount()
                        }
                    }
                    is DomainResult.Error -> {
                        // User load error - non-critical, just continue
                    }
                }
            }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getCartItemsUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        val cart = result.data
                        // Calculate total using the same cart to avoid race conditions
                        val totalInfo = calculateCartTotalUseCase(cart)
                        _uiState.update { state ->
                            state.copy(
                                cart = cart,
                                subtotal = totalInfo.subtotal,
                                deliveryFee = totalInfo.deliveryFee,
                                totalPrice = totalInfo.total,
                                isLoading = false
                            )
                        }
                        // Recalculate discount if usePoints is enabled
                        if (_uiState.value.usePoints) {
                            calculatePointsDiscount()
                        }
                        // Load recommendations based on cart
                        loadRecommendations(cart)
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

    /**
     * Toggle use points for discount
     */
    private fun toggleUsePoints(usePoints: Boolean) {
        _uiState.update { it.copy(usePoints = usePoints) }
        if (usePoints) {
            calculatePointsDiscount()
        } else {
            _uiState.update { it.copy(pointsDiscount = 0.0, pointsToUse = 0) }
        }
    }

    /**
     * Calculate discount amount when using points
     * Conversion: 1 Point = 100 VND
     */
    private fun calculatePointsDiscount() {
        val state = _uiState.value
        val availablePoints = state.availablePoints
        val totalPrice = state.totalPrice

        if (availablePoints <= 0 || totalPrice <= 0) {
            _uiState.update { it.copy(pointsDiscount = 0.0, pointsToUse = 0) }
            return
        }

        // Calculate max discount possible
        val maxDiscountFromPoints = availablePoints * CartUiState.POINTS_TO_VND_RATE
        val actualDiscount = minOf(maxDiscountFromPoints, totalPrice)
        val pointsToUse = (actualDiscount / CartUiState.POINTS_TO_VND_RATE).toInt()

        _uiState.update {
            it.copy(
                pointsDiscount = actualDiscount,
                pointsToUse = pointsToUse
            )
        }
    }

    /**
     * Load product recommendations based on current cart items
     */
    private fun loadRecommendations(cart: Cart) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRecommendations = true) }

            when (val result = getRecommendationsUseCase(cart)) {
                is DomainResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            recommendations = result.data,
                            isLoadingRecommendations = false
                        )
                    }
                }
                is DomainResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            recommendations = emptyList(),
                            isLoadingRecommendations = false
                        )
                    }
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

            val state = _uiState.value
            val result = placeOrderUseCase(
                note = note,
                deliveryAddress = address,
                usePoints = state.usePoints,
                pointsToUse = state.pointsToUse
            )

            when (result) {
                is DomainResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isCheckingOut = false,
                            orderSuccess = true,
                            orderId = result.data.order.id,
                            pointsEarned = result.data.pointsEarned,
                            // Reset points usage after order
                            usePoints = false,
                            pointsDiscount = 0.0,
                            pointsToUse = 0
                        )
                    }
                }
                is DomainResult.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
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
