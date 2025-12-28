package com.example.thecodecup.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thecodecup.di.ServiceLocator

/**
 * ViewModelFactory for DetailViewModel
 */
class DetailViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("DetailViewModelFactory", "create() called for ${modelClass.name}")

        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return try {
                Log.d("DetailViewModelFactory", "Getting GetCoffeeByIdUseCase...")
                val getCoffeeByIdUseCase = ServiceLocator.provideGetCoffeeByIdUseCase()
                Log.d("DetailViewModelFactory", "GetCoffeeByIdUseCase obtained")

                Log.d("DetailViewModelFactory", "Getting AddToCartUseCase...")
                val addToCartUseCase = ServiceLocator.provideAddToCartUseCase()
                Log.d("DetailViewModelFactory", "AddToCartUseCase obtained")

                Log.d("DetailViewModelFactory", "Creating DetailViewModel...")
                val viewModel = DetailViewModel(
                    getCoffeeByIdUseCase = getCoffeeByIdUseCase,
                    addToCartUseCase = addToCartUseCase
                )
                Log.d("DetailViewModelFactory", "DetailViewModel created successfully")
                viewModel as T
            } catch (e: Exception) {
                Log.e("DetailViewModelFactory", "Error creating DetailViewModel", e)
                throw e
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

