package com.example.thecodecup.data.repository

import com.example.thecodecup.data.local.dao.CartDao
import com.example.thecodecup.data.local.entity.CartItemEntity
import com.example.thecodecup.data.mapper.toDomainCartItems
import com.example.thecodecup.data.mapper.toJsonString
import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeOptions
import com.example.thecodecup.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToLong

/**
 * Implementation of CartRepository using Room Database
 *
 * Uses Long arithmetic for price calculations to avoid floating-point
 * precision errors with VND currency.
 */
class CartRepositoryImpl(
    private val cartDao: CartDao
) : CartRepository {

    override fun observeCart(): Flow<Cart> {
        return cartDao.getAllItems().map { entities ->
            Cart(items = entities.toDomainCartItems())
        }
    }

    override suspend fun getCart(): Cart {
        val entities = cartDao.getAllItemsList()
        return Cart(items = entities.toDomainCartItems())
    }

    override suspend fun addItem(
        coffee: Coffee,
        options: CoffeeOptions,
        quantity: Int
    ): CartItem {
        val optionsJson = options.toJsonString()

        // Check if item with same coffee and options already exists
        val existingItem = cartDao.findItemByCoffeeAndOptions(coffee.id, optionsJson)

        return if (existingItem != null) {
            // Update existing item quantity
            val newQuantity = existingItem.quantity + quantity
            cartDao.updateQuantity(existingItem.id, newQuantity)

            // Return updated CartItem
            CartItem(
                id = existingItem.id,
                coffee = coffee,
                options = options,
                quantity = newQuantity
            )
        } else {
            // Create new cart item
            // Use Long arithmetic to avoid floating-point precision errors
            val basePriceLong = coffee.basePrice.roundToLong()
            val extraPriceLong = options.calculateExtraPrice().roundToLong()
            val unitPrice = (basePriceLong + extraPriceLong).toDouble()

            val entity = CartItemEntity(
                id = 0, // Auto-generate
                coffeeId = coffee.id,
                coffeeName = coffee.name,
                coffeeBasePrice = coffee.basePrice,
                quantity = quantity,
                selectedOptions = optionsJson,
                unitPrice = unitPrice
            )

            val insertedId = cartDao.insert(entity).toInt()

            CartItem(
                id = insertedId,
                coffee = coffee,
                options = options,
                quantity = quantity
            )
        }
    }

    override suspend fun updateItemQuantity(itemId: Int, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteById(itemId)
        } else {
            cartDao.updateQuantity(itemId, quantity)
        }
    }

    override suspend fun removeItem(itemId: Int) {
        cartDao.deleteById(itemId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override suspend fun getItemCount(): Int {
        return cartDao.getTotalItemCount()
    }

    override suspend fun getTotalPrice(): Double {
        return cartDao.getTotalPrice()
    }
}

