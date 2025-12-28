package com.example.thecodecup.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thecodecup.di.ServiceLocator

/**
 * ViewModelFactory for HomeViewModel
 */
class HomeViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return try {
                HomeViewModel(
                    getCoffeeMenuUseCase = ServiceLocator.provideGetCoffeeMenuUseCase(),
                    getCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase(),
                    addToCartUseCase = ServiceLocator.provideAddToCartUseCase()
                ) as T
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModelFactory", "Error creating HomeViewModel", e)
                throw e
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

