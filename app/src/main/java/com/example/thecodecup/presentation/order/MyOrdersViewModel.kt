package com.example.thecodecup.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.di.ServiceLocator
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.usecase.order.GetOngoingOrdersUseCase
import com.example.thecodecup.domain.usecase.order.GetOrderHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for My Orders Screen
 * Uses UseCases instead of Repositories directly
 */
class MyOrdersViewModel(
    private val getOrderHistoryUseCase: GetOrderHistoryUseCase = ServiceLocator.provideGetOrderHistoryUseCase(),
    private val getOngoingOrdersUseCase: GetOngoingOrdersUseCase = ServiceLocator.provideGetOngoingOrdersUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyOrdersUiState())
    val uiState: StateFlow<MyOrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun onEvent(event: MyOrdersUiEvent) {
        when (event) {
            is MyOrdersUiEvent.SelectTab -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            is MyOrdersUiEvent.RefreshOrders -> loadOrders()
            is MyOrdersUiEvent.ClearError -> clearError()
            is MyOrdersUiEvent.OrderClicked -> { /* Handled by screen */ }
        }
    }

    private fun loadOrders() {
        loadOngoingOrders()
        loadOrderHistory()
    }

    private fun loadOngoingOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getOngoingOrdersUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        _uiState.update { state ->
                            state.copy(ongoingOrders = result.data)
                        }
                    }
                    is DomainResult.Error -> {
                        _uiState.update { state ->
                            state.copy(errorMessage = result.exception.message)
                        }
                    }
                }
            }
        }
    }

    private fun loadOrderHistory() {
        viewModelScope.launch {
            getOrderHistoryUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                historyOrders = result.data,
                                isLoading = false
                            )
                        }
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

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
