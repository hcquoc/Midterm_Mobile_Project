package com.example.thecodecup.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thecodecup.di.ServiceLocator

/**
 * ViewModelFactory for CartViewModel
 */
class CartViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return try {
                CartViewModel(
                    getCartItemsUseCase = ServiceLocator.provideGetCartItemsUseCase(),
                    calculateCartTotalUseCase = ServiceLocator.provideCalculateCartTotalUseCase(),
                    updateCartItemQuantityUseCase = ServiceLocator.provideUpdateCartItemQuantityUseCase(),
                    removeFromCartUseCase = ServiceLocator.provideRemoveFromCartUseCase(),
                    placeOrderUseCase = ServiceLocator.providePlaceOrderUseCase()
                ) as T
            } catch (e: Exception) {
                android.util.Log.e("CartViewModelFactory", "Error creating CartViewModel", e)
                throw e
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

