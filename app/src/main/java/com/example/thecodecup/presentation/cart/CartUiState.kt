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
    val pointsToUse: Int = 0,
    // Voucher System
    val isVoucherApplied: Boolean = false,
    val voucherDiscount: Double = 0.0
) {
    companion object {
        /** Conversion rate: 1 Point = 100 VND */
        const val POINTS_TO_VND_RATE = 100.0
        /** Voucher value: 1 Voucher = 2,000 VND */
        const val VOUCHER_VALUE = 2000.0
    }

    val items: List<CartItem> get() = cart.items
    val isEmpty: Boolean get() = cart.isEmpty
    val hasRecommendations: Boolean get() = recommendations.isNotEmpty()

    /** User's available points */
    val availablePoints: Int get() = user?.rewardPoints ?: 0

    /** User's available vouchers */
    val availableVouchers: Int get() = user?.voucherCount ?: 0

    /** User's membership tier */
    val membershipTier: MembershipTier get() = user?.membershipTier ?: MembershipTier.SILVER

    /** Maximum discount possible with user's points */
    val maxPointsDiscount: Double
        get() = minOf(availablePoints * POINTS_TO_VND_RATE, subtotal + deliveryFee)

    /** Maximum points that can be used for this order */
    val maxPointsToUse: Int
        get() = minOf(availablePoints, ((subtotal + deliveryFee) / POINTS_TO_VND_RATE).toInt())

    /** Check if user can apply voucher */
    val canApplyVoucher: Boolean get() = availableVouchers > 0 && !isEmpty

    /** Total discount (points + voucher) */
    val totalDiscount: Double
        get() = pointsDiscount + voucherDiscount

    /** Final total after all discounts */
    val finalTotal: Double
        get() {
            val afterPointsDiscount = if (usePoints) totalPrice - pointsDiscount else totalPrice
            val afterVoucherDiscount = if (isVoucherApplied && canApplyVoucher) afterPointsDiscount - VOUCHER_VALUE else afterPointsDiscount
            return afterVoucherDiscount.coerceAtLeast(0.0)
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
    // Voucher events
    data class ToggleVoucher(val apply: Boolean) : CartUiEvent
}
