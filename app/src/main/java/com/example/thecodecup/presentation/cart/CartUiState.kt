package com.example.thecodecup.presentation.cart

import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.MembershipTier
import com.example.thecodecup.domain.model.User

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
    val errorMessage: String? = null,
    // Recommendations
    val recommendations: List<Coffee> = emptyList(),
    val isLoadingRecommendations: Boolean = false,
    // Pay with Points
    val user: User? = null,
    val usePoints: Boolean = false,
    val pointsDiscount: Double = 0.0,
    val pointsToUse: Int = 0
) {
    companion object {
        /** Conversion rate: 1 Point = 100 VND */
        const val POINTS_TO_VND_RATE = 100.0
    }

    val items: List<CartItem> get() = cart.items
    val isEmpty: Boolean get() = cart.isEmpty
    val hasRecommendations: Boolean get() = recommendations.isNotEmpty()

    /** User's available points */
    val availablePoints: Int get() = user?.rewardPoints ?: 0

    /** User's membership tier */
    val membershipTier: MembershipTier get() = user?.membershipTier ?: MembershipTier.SILVER

    /** Maximum discount possible with user's points */
    val maxPointsDiscount: Double
        get() = minOf(availablePoints * POINTS_TO_VND_RATE, subtotal + deliveryFee)

    /** Maximum points that can be used for this order */
    val maxPointsToUse: Int
        get() = minOf(availablePoints, ((subtotal + deliveryFee) / POINTS_TO_VND_RATE).toInt())

    /** Final total after points discount */
    val finalTotal: Double
        get() = if (usePoints) {
            (totalPrice - pointsDiscount).coerceAtLeast(0.0)
        } else {
            totalPrice
        }

    /** Check if user can use points */
    val canUsePoints: Boolean get() = availablePoints > 0 && !isEmpty
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
    // Pay with Points events
    data class ToggleUsePoints(val usePoints: Boolean) : CartUiEvent
}
