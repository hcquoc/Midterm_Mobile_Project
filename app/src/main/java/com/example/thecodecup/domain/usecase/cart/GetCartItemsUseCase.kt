package com.example.thecodecup.domain.usecase.cart

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Cart
import com.example.thecodecup.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Use case to get cart items
 */
class GetCartItemsUseCase(
    private val cartRepository: CartRepository
) {

    /**
     * Execute the use case to observe cart
     * @return Flow of DomainResult containing Cart
     */
    operator fun invoke(): Flow<DomainResult<Cart>> {
        return cartRepository.observeCart()
            .map<Cart, DomainResult<Cart>> { cart ->
                DomainResult.Success(cart)
            }
            .catch { exception ->
                emit(
                    DomainResult.Error(
                        DomainException.DatabaseException(
                            message = "Failed to load cart: ${exception.message}",
                            cause = exception
                        )
                    )
                )
            }
    }

    /**
     * Get cart without observing (one-time fetch)
     * @return DomainResult containing Cart
     */
    suspend fun getOnce(): DomainResult<Cart> {
        return try {
            val cart = cartRepository.getCart()
            DomainResult.Success(cart)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to load cart: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

