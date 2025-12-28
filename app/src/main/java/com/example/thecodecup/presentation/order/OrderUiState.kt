package com.example.thecodecup.presentation.order

import com.example.thecodecup.domain.model.Order

/**
 * UI State for My Orders Screen
 */
data class MyOrdersUiState(
    val selectedTab: Int = 0, // 0 = Ongoing, 1 = History
    val ongoingOrders: List<Order> = emptyList(),
    val historyOrders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Get ongoing orders as display items
     */
    val ongoingDisplayItems: List<OrderDisplayItem>
        get() = ongoingOrders.map { OrderDisplayItem.fromOrder(it) }

    /**
     * Get history orders as display items
     */
    val historyDisplayItems: List<OrderDisplayItem>
        get() = historyOrders.map { OrderDisplayItem.fromOrder(it) }
}

/**
 * Events from My Orders Screen
 */
sealed interface MyOrdersUiEvent {
    data class SelectTab(val tabIndex: Int) : MyOrdersUiEvent
    data object RefreshOrders : MyOrdersUiEvent
    data object ClearError : MyOrdersUiEvent
    data class OrderClicked(val order: Order) : MyOrdersUiEvent
}
