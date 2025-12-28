package com.example.thecodecup.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thecodecup.di.ServiceLocator
import com.example.thecodecup.presentation.cart.CartViewModel
import com.example.thecodecup.presentation.detail.DetailViewModel
import com.example.thecodecup.presentation.home.HomeViewModel
import com.example.thecodecup.presentation.order.MyOrdersViewModel
import com.example.thecodecup.presentation.profile.ProfileViewModel
import com.example.thecodecup.presentation.rewards.RedeemViewModel
import com.example.thecodecup.presentation.rewards.RewardsViewModel

/**
 * ViewModelFactory that provides UseCases from ServiceLocator to ViewModels.
 * Ensures ViewModels depend ONLY on UseCases, not Repositories.
 */
class AppViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    getCoffeeMenuUseCase = ServiceLocator.provideGetCoffeeMenuUseCase(),
                    getCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(
                    getCoffeeByIdUseCase = ServiceLocator.provideGetCoffeeByIdUseCase(),
                    addToCartUseCase = ServiceLocator.provideAddToCartUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(
                    getCartItemsUseCase = ServiceLocator.provideGetCartItemsUseCase(),
                    calculateCartTotalUseCase = ServiceLocator.provideCalculateCartTotalUseCase(),
                    updateCartItemQuantityUseCase = ServiceLocator.provideUpdateCartItemQuantityUseCase(),
                    removeFromCartUseCase = ServiceLocator.provideRemoveFromCartUseCase(),
                    placeOrderUseCase = ServiceLocator.providePlaceOrderUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    getCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase(),
                    updateUserProfileUseCase = ServiceLocator.provideUpdateUserProfileUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(MyOrdersViewModel::class.java) -> {
                MyOrdersViewModel(
                    getOrderHistoryUseCase = ServiceLocator.provideGetOrderHistoryUseCase(),
                    getOngoingOrdersUseCase = ServiceLocator.provideGetOngoingOrdersUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(RewardsViewModel::class.java) -> {
                RewardsViewModel(
                    getCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(RedeemViewModel::class.java) -> {
                RedeemViewModel(
                    getCurrentUserUseCase = ServiceLocator.provideGetCurrentUserUseCase(),
                    rewardRepository = ServiceLocator.provideRewardRepository()
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

