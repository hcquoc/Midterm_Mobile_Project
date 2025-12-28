package com.example.thecodecup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thecodecup.presentation.cart.CartScreen
import com.example.thecodecup.presentation.cart.CartViewModel
import com.example.thecodecup.presentation.cart.CartViewModelFactory
import com.example.thecodecup.presentation.detail.DetailScreen
import com.example.thecodecup.presentation.home.HomeScreen
import com.example.thecodecup.presentation.order.MyOrderScreen
import com.example.thecodecup.presentation.order.OrderSuccessScreen
import com.example.thecodecup.presentation.order.OrderTrackingScreen
import com.example.thecodecup.presentation.profile.ProfileScreen
import com.example.thecodecup.presentation.rewards.RedeemScreen
import com.example.thecodecup.presentation.rewards.RewardsScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Navigation route definitions
object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{coffeeId}/{coffeeName}"
    const val CART = "cart"
    const val ORDER_SUCCESS = "order_success/{orderId}"
    const val ORDER_TRACKING = "order_tracking/{orderId}"
    const val PROFILE = "profile"
    const val REWARDS = "rewards"
    const val REDEEM = "redeem"
    const val MY_ORDERS = "my_orders"

    fun detailRoute(coffeeId: Int, coffeeName: String): String {
        val encodedName = URLEncoder.encode(coffeeName, StandardCharsets.UTF_8.toString())
        return "detail/$coffeeId/$encodedName"
    }

    fun orderSuccessRoute(orderId: String): String {
        return "order_success/$orderId"
    }

    fun orderTrackingRoute(orderId: String): String {
        return "order_tracking/$orderId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.HOME
) {
    // Shared CartViewModel for cart item count across screens
    val cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory())
    val cartItemCount by cartViewModel.cartItemCount.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Home Screen
        composable(Routes.HOME) {
            HomeScreen(
                onCoffeeClick = { coffee ->
                    try {
                        android.util.Log.d("AppNavGraph", "Navigating to detail: id=${coffee.id}, name=${coffee.name}")
                        val route = Routes.detailRoute(coffee.id, coffee.name)
                        android.util.Log.d("AppNavGraph", "Route: $route")
                        navController.navigate(route)
                    } catch (e: Exception) {
                        android.util.Log.e("AppNavGraph", "Navigation error", e)
                    }
                },
                onAddToCartClick = { coffee ->
                    // Navigate to detail screen for quick add (can be enhanced later)
                    try {
                        val route = Routes.detailRoute(coffee.id, coffee.name)
                        navController.navigate(route)
                    } catch (e: Exception) {
                        android.util.Log.e("AppNavGraph", "Add to cart navigation error", e)
                    }
                },
                onCartClick = {
                    navController.navigate(Routes.CART)
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                },
                onRewardsClick = {
                    navController.navigate(Routes.REWARDS)
                },
                onOrdersClick = {
                    navController.navigate(Routes.MY_ORDERS)
                },
                cartItemCount = cartItemCount
            )
        }

        // Detail Screen with arguments
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("coffeeId") { type = NavType.IntType },
                navArgument("coffeeName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            android.util.Log.d("AppNavGraph", "Detail composable entered")
            val coffeeId = backStackEntry.arguments?.getInt("coffeeId") ?: 0
            val encodedName = backStackEntry.arguments?.getString("coffeeName") ?: ""
            android.util.Log.d("AppNavGraph", "coffeeId=$coffeeId, encodedName=$encodedName")

            val coffeeName = try {
                URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                android.util.Log.e("AppNavGraph", "Error decoding name", e)
                encodedName
            }
            android.util.Log.d("AppNavGraph", "Decoded coffeeName=$coffeeName")

            android.util.Log.d("AppNavGraph", "About to render DetailScreen")
            DetailScreen(
                coffeeId = coffeeId,
                coffeeName = coffeeName,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Routes.CART)
                }
            )
            android.util.Log.d("AppNavGraph", "DetailScreen rendered")
        }

        // Cart Screen
        composable(Routes.CART) {
            CartScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckout = { orderId ->
                    navController.navigate(Routes.orderSuccessRoute(orderId)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onCoffeeClick = { coffee ->
                    // Navigate to detail screen when clicking on recommendation
                    try {
                        val route = Routes.detailRoute(coffee.id, coffee.name)
                        navController.navigate(route)
                    } catch (e: Exception) {
                        android.util.Log.e("AppNavGraph", "Recommendation navigation error", e)
                    }
                }
            )
        }

        // Order Success Screen
        composable(
            route = Routes.ORDER_SUCCESS,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderSuccessScreen(
                orderId = orderId,
                onTrackOrder = {
                    // Navigate to Order Tracking Screen
                    navController.navigate(Routes.orderTrackingRoute(orderId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        // Order Tracking Screen
        composable(
            route = Routes.ORDER_TRACKING,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderTrackingScreen(
                orderId = orderId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Profile Screen
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Rewards Screen
        composable(Routes.REWARDS) {
            RewardsScreen(
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onRewardsClick = { /* Already on rewards */ },
                onOrdersClick = {
                    navController.navigate(Routes.MY_ORDERS)
                },
                onRedeemClick = {
                    navController.navigate(Routes.REDEEM)
                }
            )
        }

        // Redeem Screen
        composable(Routes.REDEEM) {
            RedeemScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // My Orders Screen
        composable(Routes.MY_ORDERS) {
            MyOrderScreen(
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onRewardsClick = {
                    navController.navigate(Routes.REWARDS)
                },
                onOrdersClick = { /* Already on orders */ }
            )
        }
    }
}
