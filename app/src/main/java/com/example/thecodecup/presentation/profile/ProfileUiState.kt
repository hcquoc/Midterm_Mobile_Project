package com.example.thecodecup.presentation.profile

import com.example.thecodecup.domain.model.MembershipTier
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.presentation.utils.PriceFormatter

/**
 * UI State for Profile Screen
 */
data class ProfileUiState(
    val fullName: String = "Anderson",
    val phoneNumber: String = "+84912345678",
    val email: String = "Anderson@email.com",
    val address: String = "123 Nguyễn Huệ, Quận 1\nTP. Hồ Chí Minh, Việt Nam",
    val avatarInitials: String = "A",
    val rewardPoints: Int = 0,
    val totalSpent: Double = 0.0,
    val membershipTier: MembershipTier = MembershipTier.SILVER,
    val pointsToNextTier: Int = 1000,
    val tierProgress: Float = 0f,
    val orderHistory: List<Order> = emptyList(),
    val isLoadingOrders: Boolean = false,
    val editingField: ProfileField? = null,
    val editValue: String = "",
    val isSaving: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val formattedTotalSpent: String
        get() = PriceFormatter.formatVND(totalSpent)

    val completedOrdersCount: Int
        get() = orderHistory.size

    /** Check if user is at highest tier */
    val isMaxTier: Boolean
        get() = membershipTier == MembershipTier.GOLD
}

/**
 * Profile fields that can be edited
 */
enum class ProfileField {
    FULL_NAME,
    PHONE_NUMBER,
    EMAIL,
    ADDRESS
}

/**
 * Events from Profile Screen
 */
sealed interface ProfileUiEvent {
    data class StartEditing(val field: ProfileField) : ProfileUiEvent
    data class UpdateEditValue(val value: String) : ProfileUiEvent
    data object SaveEdit : ProfileUiEvent
    data object CancelEdit : ProfileUiEvent
    data object ConsumeUpdateSuccess : ProfileUiEvent
    data object ClearError : ProfileUiEvent
    data object NavigateBack : ProfileUiEvent
    data object LogOut : ProfileUiEvent
}
