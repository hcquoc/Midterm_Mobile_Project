package com.example.thecodecup.di

import android.content.Context
import com.example.thecodecup.data.local.AppDatabase
import com.example.thecodecup.data.local.dao.CartDao
import com.example.thecodecup.data.local.dao.CoffeeDao
import com.example.thecodecup.data.local.dao.OrderDao
import com.example.thecodecup.data.repository.CartRepositoryImpl
import com.example.thecodecup.data.repository.CoffeeRepositoryImpl
import com.example.thecodecup.data.repository.OrderRepositoryImpl
import com.example.thecodecup.data.repository.RewardRepositoryImpl
import com.example.thecodecup.data.repository.UserRepositoryImpl
import com.example.thecodecup.domain.repository.CartRepository
import com.example.thecodecup.domain.repository.CoffeeRepository
import com.example.thecodecup.domain.repository.OrderRepository
import com.example.thecodecup.domain.repository.RewardRepository
import com.example.thecodecup.domain.repository.UserRepository
import com.example.thecodecup.domain.usecase.cart.AddToCartUseCase
import com.example.thecodecup.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.thecodecup.domain.usecase.cart.ClearCartUseCase
import com.example.thecodecup.domain.usecase.cart.GetCartItemsUseCase
import com.example.thecodecup.domain.usecase.cart.RemoveFromCartUseCase
import com.example.thecodecup.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.example.thecodecup.domain.usecase.coffee.GetCoffeeByIdUseCase
import com.example.thecodecup.domain.usecase.coffee.GetCoffeeMenuUseCase
import com.example.thecodecup.domain.usecase.order.CancelOrderUseCase
import com.example.thecodecup.domain.usecase.order.GetOngoingOrdersUseCase
import com.example.thecodecup.domain.usecase.order.GetOrderByIdUseCase
import com.example.thecodecup.domain.usecase.order.GetOrderHistoryUseCase
import com.example.thecodecup.domain.usecase.order.PlaceOrderUseCase
import com.example.thecodecup.domain.usecase.recommendation.GetRecommendationsUseCase
import com.example.thecodecup.domain.usecase.user.GetCurrentUserUseCase
import com.example.thecodecup.domain.usecase.user.UpdateUserProfileUseCase

/**
 * Simple Service Locator for dependency injection
 * In a production app, consider using Hilt or Koin
 */
object ServiceLocator {

    // Application context for lazy initialization
    private var appContext: Context? = null

    // Database instance
    private var database: AppDatabase? = null

    // DAO instances
    private var coffeeDao: CoffeeDao? = null
    private var cartDao: CartDao? = null
    private var orderDao: OrderDao? = null

    // Repository instances
    private var coffeeRepository: CoffeeRepository? = null
    private var cartRepository: CartRepository? = null
    private var orderRepository: OrderRepository? = null
    private var userRepository: UserRepository? = null
    private var rewardRepository: RewardRepository? = null

    // Use case instances - Coffee
    private var getCoffeeMenuUseCase: GetCoffeeMenuUseCase? = null
    private var getCoffeeByIdUseCase: GetCoffeeByIdUseCase? = null

    // Use case instances - Cart
    private var addToCartUseCase: AddToCartUseCase? = null
    private var getCartItemsUseCase: GetCartItemsUseCase? = null
    private var updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase? = null
    private var calculateCartTotalUseCase: CalculateCartTotalUseCase? = null
    private var removeFromCartUseCase: RemoveFromCartUseCase? = null
    private var clearCartUseCase: ClearCartUseCase? = null

    // Use case instances - User
    private var getCurrentUserUseCase: GetCurrentUserUseCase? = null
    private var updateUserProfileUseCase: UpdateUserProfileUseCase? = null

    // Use case instances - Order
    private var placeOrderUseCase: PlaceOrderUseCase? = null
    private var getOrderHistoryUseCase: GetOrderHistoryUseCase? = null
    private var getOngoingOrdersUseCase: GetOngoingOrdersUseCase? = null
    private var getOrderByIdUseCase: GetOrderByIdUseCase? = null
    private var cancelOrderUseCase: CancelOrderUseCase? = null

    // Use case instances - Recommendation
    private var getRecommendationsUseCase: GetRecommendationsUseCase? = null

    /**
     * Initialize the ServiceLocator with application context
     * Call this in Application.onCreate()
     */
    fun initialize(context: Context) {
        android.util.Log.d("ServiceLocator", "initialize() called")
        appContext = context.applicationContext
        // Lazy initialize database - will be created when first accessed
        ensureDatabaseInitialized()
        android.util.Log.d("ServiceLocator", "initialize() completed")
    }

    /**
     * Ensure database is initialized
     */
    private fun ensureDatabaseInitialized() {
        if (database == null && appContext != null) {
            android.util.Log.d("ServiceLocator", "Creating database instance...")
            database = AppDatabase.getInstance(appContext!!)
            coffeeDao = database!!.coffeeDao()
            cartDao = database!!.cartDao()
            orderDao = database!!.orderDao()
            android.util.Log.d("ServiceLocator", "Database initialized successfully")
        }
    }

    // ==================== Database & DAOs ====================

    fun provideDatabase(): AppDatabase {
        ensureDatabaseInitialized()
        return database ?: throw IllegalStateException(
            "ServiceLocator not initialized. Call initialize(context) first."
        )
    }

    fun provideCoffeeDao(): CoffeeDao {
        ensureDatabaseInitialized()
        return coffeeDao ?: throw IllegalStateException(
            "ServiceLocator not initialized. Call initialize(context) first."
        )
    }

    fun provideCartDao(): CartDao {
        ensureDatabaseInitialized()
        return cartDao ?: throw IllegalStateException(
            "ServiceLocator not initialized. Call initialize(context) first."
        )
    }

    fun provideOrderDao(): OrderDao {
        ensureDatabaseInitialized()
        return orderDao ?: throw IllegalStateException(
            "ServiceLocator not initialized. Call initialize(context) first."
        )
    }

    // ==================== Repositories ====================

    fun provideCoffeeRepository(): CoffeeRepository {
        return coffeeRepository ?: CoffeeRepositoryImpl(
            coffeeDao = provideCoffeeDao()
        ).also {
            coffeeRepository = it
        }
    }

    fun provideCartRepository(): CartRepository {
        return cartRepository ?: CartRepositoryImpl(
            cartDao = provideCartDao()
        ).also {
            cartRepository = it
        }
    }

    fun provideOrderRepository(): OrderRepository {
        return orderRepository ?: OrderRepositoryImpl(
            orderDao = provideOrderDao()
        ).also {
            orderRepository = it
        }
    }

    fun provideUserRepository(): UserRepository {
        return userRepository ?: UserRepositoryImpl().also {
            userRepository = it
        }
    }

    fun provideRewardRepository(): RewardRepository {
        return rewardRepository ?: RewardRepositoryImpl(
            userRepository = provideUserRepository()
        ).also {
            rewardRepository = it
        }
    }

    // ==================== Use Cases - Coffee ====================

    fun provideGetCoffeeMenuUseCase(): GetCoffeeMenuUseCase {
        return getCoffeeMenuUseCase ?: GetCoffeeMenuUseCase(
            coffeeRepository = provideCoffeeRepository()
        ).also {
            getCoffeeMenuUseCase = it
        }
    }

    fun provideGetCoffeeByIdUseCase(): GetCoffeeByIdUseCase {
        return getCoffeeByIdUseCase ?: GetCoffeeByIdUseCase(
            coffeeRepository = provideCoffeeRepository()
        ).also {
            getCoffeeByIdUseCase = it
        }
    }

    // ==================== Use Cases - Cart ====================

    fun provideAddToCartUseCase(): AddToCartUseCase {
        return addToCartUseCase ?: AddToCartUseCase(
            cartRepository = provideCartRepository()
        ).also {
            addToCartUseCase = it
        }
    }

    fun provideGetCartItemsUseCase(): GetCartItemsUseCase {
        return getCartItemsUseCase ?: GetCartItemsUseCase(
            cartRepository = provideCartRepository()
        ).also {
            getCartItemsUseCase = it
        }
    }

    fun provideUpdateCartItemQuantityUseCase(): UpdateCartItemQuantityUseCase {
        return updateCartItemQuantityUseCase ?: UpdateCartItemQuantityUseCase(
            cartRepository = provideCartRepository()
        ).also {
            updateCartItemQuantityUseCase = it
        }
    }

    fun provideCalculateCartTotalUseCase(): CalculateCartTotalUseCase {
        return calculateCartTotalUseCase ?: CalculateCartTotalUseCase(
            cartRepository = provideCartRepository()
        ).also {
            calculateCartTotalUseCase = it
        }
    }

    fun provideRemoveFromCartUseCase(): RemoveFromCartUseCase {
        return removeFromCartUseCase ?: RemoveFromCartUseCase(
            cartRepository = provideCartRepository()
        ).also {
            removeFromCartUseCase = it
        }
    }

    fun provideClearCartUseCase(): ClearCartUseCase {
        return clearCartUseCase ?: ClearCartUseCase(
            cartRepository = provideCartRepository()
        ).also {
            clearCartUseCase = it
        }
    }

    // ==================== Use Cases - User ====================

    fun provideGetCurrentUserUseCase(): GetCurrentUserUseCase {
        return getCurrentUserUseCase ?: GetCurrentUserUseCase(
            userRepository = provideUserRepository()
        ).also {
            getCurrentUserUseCase = it
        }
    }

    fun provideUpdateUserProfileUseCase(): UpdateUserProfileUseCase {
        return updateUserProfileUseCase ?: UpdateUserProfileUseCase(
            userRepository = provideUserRepository()
        ).also {
            updateUserProfileUseCase = it
        }
    }

    // ==================== Use Cases - Order ====================

    fun providePlaceOrderUseCase(): PlaceOrderUseCase {
        return placeOrderUseCase ?: PlaceOrderUseCase(
            cartRepository = provideCartRepository(),
            orderRepository = provideOrderRepository(),
            userRepository = provideUserRepository()
        ).also {
            placeOrderUseCase = it
        }
    }

    fun provideGetOrderHistoryUseCase(): GetOrderHistoryUseCase {
        return getOrderHistoryUseCase ?: GetOrderHistoryUseCase(
            orderRepository = provideOrderRepository()
        ).also {
            getOrderHistoryUseCase = it
        }
    }

    fun provideGetOngoingOrdersUseCase(): GetOngoingOrdersUseCase {
        return getOngoingOrdersUseCase ?: GetOngoingOrdersUseCase(
            orderRepository = provideOrderRepository()
        ).also {
            getOngoingOrdersUseCase = it
        }
    }

    fun provideGetOrderByIdUseCase(): GetOrderByIdUseCase {
        return getOrderByIdUseCase ?: GetOrderByIdUseCase(
            orderRepository = provideOrderRepository()
        ).also {
            getOrderByIdUseCase = it
        }
    }

    fun provideCancelOrderUseCase(): CancelOrderUseCase {
        return cancelOrderUseCase ?: CancelOrderUseCase(
            orderRepository = provideOrderRepository()
        ).also {
            cancelOrderUseCase = it
        }
    }

    // ==================== Use Cases - Recommendation ====================

    fun provideGetRecommendationsUseCase(): GetRecommendationsUseCase {
        return getRecommendationsUseCase ?: GetRecommendationsUseCase(
            coffeeRepository = provideCoffeeRepository()
        ).also {
            getRecommendationsUseCase = it
        }
    }

    /**
     * Reset all instances (useful for testing)
     */
    fun resetAll() {
        appContext = null
        database = null
        coffeeDao = null
        cartDao = null
        orderDao = null
        coffeeRepository = null
        cartRepository = null
        orderRepository = null
        userRepository = null
        rewardRepository = null
        // Coffee use cases
        getCoffeeMenuUseCase = null
        getCoffeeByIdUseCase = null
        // Cart use cases
        addToCartUseCase = null
        getCartItemsUseCase = null
        updateCartItemQuantityUseCase = null
        calculateCartTotalUseCase = null
        removeFromCartUseCase = null
        clearCartUseCase = null
        // User use cases
        getCurrentUserUseCase = null
        updateUserProfileUseCase = null
        // Order use cases
        placeOrderUseCase = null
        getOrderHistoryUseCase = null
        getOngoingOrdersUseCase = null
        getOrderByIdUseCase = null
        cancelOrderUseCase = null
        // Recommendation use cases
        getRecommendationsUseCase = null
    }
}