package com.example.thecodecup.domain.usecase.order

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.repository.OrderRepository

/**
 * Use case to get order details by ID
 */
class GetOrderByIdUseCase(
    private val orderRepository: OrderRepository
) {

    /**
     * Execute the use case to get order by ID
     * @param orderId The ID of the order to retrieve
     * @return DomainResult containing Order or error
     */
    suspend operator fun invoke(orderId: String): DomainResult<Order> {
        return try {
            val order = orderRepository.getOrderById(orderId)

            if (order != null) {
                DomainResult.Success(order)
            } else {
                DomainResult.Error(
                    DomainException.OrderNotFoundException(orderId)
                )
            }
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to get order: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

