package com.example.thecodecup.domain.usecase.recommendation

import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeCategory
import com.example.thecodecup.domain.repository.CoffeeRepository
import kotlinx.coroutines.flow.first

/**
 * Use case to get product recommendations based on cart items
 *
 * Logic:
 * 1. If cart contains drinks (Coffee, Latte, Tea, Espresso, Phin, Freeze) -> Suggest CAKE items
 * 2. If cart contains only CAKE items -> Suggest drink items
 * 3. If cart is empty or no matching pattern -> Suggest 3 random items not in cart
 */
class GetRecommendationsUseCase(
    private val coffeeRepository: CoffeeRepository
) {
    companion object {
        private const val MAX_RECOMMENDATIONS = 3

        // Keywords to identify drink items
        private val DRINK_KEYWORDS = listOf(
            "coffee", "cà phê", "latte", "tea", "trà", "espresso",
            "cappuccino", "mocha", "americano", "macchiato", "brew",
            "freeze", "bạc sỉu", "matcha"
        )

        // Keywords to identify food/cake items
        private val FOOD_KEYWORDS = listOf(
            "cake", "bánh", "cookie", "bread", "tiramisu", "mousse",
            "cupcake", "pudding", "combo"
        )

        // Drink categories
        private val DRINK_CATEGORIES = listOf(
            CoffeeCategory.COFFEE,
            CoffeeCategory.ESPRESSO,
            CoffeeCategory.LATTE,
            CoffeeCategory.TEA,
            CoffeeCategory.PHIN,
            CoffeeCategory.FREEZE
        )
    }

    /**
     * Get recommendations based on current cart
     *
     * @param cart Current cart state
     * @return List of recommended Coffee items (max 3)
     */
    suspend operator fun invoke(cart: Cart): DomainResult<List<Coffee>> {
        return try {
            val allCoffees = coffeeRepository.getAllCoffees().first()
            val cartItemIds = cart.items.map { it.coffee.id }.toSet()

            // Filter out items already in cart
            val availableItems = allCoffees.filter { it.id !in cartItemIds }

            if (availableItems.isEmpty()) {
                return DomainResult.Success(emptyList())
            }

            val recommendations = when {
                cart.isEmpty -> {
                    // Empty cart: suggest popular items (high rating)
                    availableItems
                        .sortedByDescending { it.rating }
                        .take(MAX_RECOMMENDATIONS)
                }
                else -> {
                    // Analyze cart items
                    val hasDrinks = cart.items.any { item -> isDrinkItem(item.coffee) }
                    val hasFoods = cart.items.any { item -> isFoodItem(item.coffee) }

                    when {
                        hasDrinks && !hasFoods -> {
                            // Cart has drinks only -> Suggest food/cake items
                            val foodItems = availableItems.filter { isFoodItem(it) }
                            if (foodItems.isNotEmpty()) {
                                foodItems.shuffled().take(MAX_RECOMMENDATIONS)
                            } else {
                                getRandomRecommendations(availableItems)
                            }
                        }
                        hasFoods && !hasDrinks -> {
                            // Cart has food only -> Suggest drink items
                            val drinkItems = availableItems.filter { isDrinkItem(it) }
                            if (drinkItems.isNotEmpty()) {
                                drinkItems.shuffled().take(MAX_RECOMMENDATIONS)
                            } else {
                                getRandomRecommendations(availableItems)
                            }
                        }
                        else -> {
                            // Mixed cart or unknown -> Random recommendations
                            getRandomRecommendations(availableItems)
                        }
                    }
                }
            }

            DomainResult.Success(recommendations)
        } catch (e: Exception) {
            DomainResult.Success(emptyList()) // Return empty list on error, don't break the app
        }
    }

    /**
     * Check if a coffee item is a drink
     */
    private fun isDrinkItem(coffee: Coffee): Boolean {
        // Check by category first
        if (coffee.category in DRINK_CATEGORIES) return true

        // Check by name keywords
        val nameLower = coffee.name.lowercase()
        return DRINK_KEYWORDS.any { keyword -> nameLower.contains(keyword) }
    }

    /**
     * Check if a coffee item is a food/cake
     */
    private fun isFoodItem(coffee: Coffee): Boolean {
        // Check by category first
        if (coffee.category == CoffeeCategory.CAKE) return true

        // Check by name keywords
        val nameLower = coffee.name.lowercase()
        return FOOD_KEYWORDS.any { keyword -> nameLower.contains(keyword) }
    }

    /**
     * Get random recommendations from available items
     */
    private fun getRandomRecommendations(availableItems: List<Coffee>): List<Coffee> {
        return availableItems.shuffled().take(MAX_RECOMMENDATIONS)
    }
}

