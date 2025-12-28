package com.example.thecodecup.presentation.cart

import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.CartItem

/**
 * UI State for Cart Screen
 */
data class CartUiState(
    val cart: Cart = Cart(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 15000.0,
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val isCheckingOut: Boolean = false,
    val orderSuccess: Boolean = false,
    val orderId: String? = null,
    val pointsEarned: Int = 0,
    val errorMessage: String? = null
) {
    val items: List<CartItem> get() = cart.items
    val isEmpty: Boolean get() = cart.isEmpty
}

/**
 * Events from Cart Screen
 */
sealed interface CartUiEvent {
    data object LoadCart : CartUiEvent
    data class RemoveItem(val itemId: Int) : CartUiEvent
    data class UpdateQuantity(val itemId: Int, val quantity: Int) : CartUiEvent
    data class PlaceOrder(val note: String? = null, val address: String? = null) : CartUiEvent
    data object ConsumeOrderSuccess : CartUiEvent
    data object ClearError : CartUiEvent
    data object NavigateBack : CartUiEvent
}
