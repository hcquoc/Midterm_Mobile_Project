package com.example.thecodecup.presentation.profile

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
    val loyaltyTier: LoyaltyTier = LoyaltyTier.BRONZE,
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
}

/**
 * Loyalty tier based on total spent (VND)
 */
enum class LoyaltyTier(val displayName: String, val minSpent: Double) {
    BRONZE("Bronze", 0.0),
    SILVER("Silver", 500000.0),      // 500K VND
    GOLD("Gold", 1500000.0),         // 1.5M VND
    PLATINUM("Platinum", 5000000.0); // 5M VND

    companion object {
        fun fromTotalSpent(totalSpent: Double): LoyaltyTier {
            return entries.sortedByDescending { it.minSpent }
                .firstOrNull { totalSpent >= it.minSpent } ?: BRONZE
        }
    }
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
