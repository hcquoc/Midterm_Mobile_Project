package com.example.thecodecup.presentation.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thecodecup.di.ServiceLocator

/**
 * ViewModelFactory for RewardsViewModel
 */
class RewardsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RewardsViewModel::class.java)) {
            return RewardsViewModel(
                getCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

