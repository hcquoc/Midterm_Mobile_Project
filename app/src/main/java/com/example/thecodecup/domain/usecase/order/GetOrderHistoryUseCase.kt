package com.example.thecodecup.domain.usecase.order

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Use case to get order history
 * Returns all completed and cancelled orders, sorted by date descending
 */
class GetOrderHistoryUseCase(
    private val orderRepository: OrderRepository
) {

    /**
     * Execute the use case to observe order history
     * @return Flow of DomainResult containing list of Orders (sorted by date descending)
     */
    operator fun invoke(): Flow<DomainResult<List<Order>>> {
        return orderRepository.getOrderHistory()
            .map<List<Order>, DomainResult<List<Order>>> { orders ->
                // Sort by date descending (newest first)
                val sortedOrders = orders.sortedByDescending { it.createdAt }
                DomainResult.Success(sortedOrders)
            }
            .catch { exception ->
                emit(
                    DomainResult.Error(
                        DomainException.DatabaseException(
                            message = "Failed to load order history: ${exception.message}",
                            cause = exception
                        )
                    )
                )
            }
    }
}

