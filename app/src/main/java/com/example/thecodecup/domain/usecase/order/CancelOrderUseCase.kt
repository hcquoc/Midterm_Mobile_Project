package com.example.thecodecup.domain.usecase.order

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.OrderStatus
import com.example.thecodecup.domain.repository.OrderRepository

/**
 * Use case to cancel an order
 */
class CancelOrderUseCase(
    private val orderRepository: OrderRepository
) {

    /**
     * Execute the use case to cancel an order
     * @param orderId The ID of the order to cancel
     * @return DomainResult indicating success or error
     */
    suspend operator fun invoke(orderId: String): DomainResult<Unit> {
        return try {
            // Check if order exists
            val order = orderRepository.getOrderById(orderId)

            if (order == null) {
                return DomainResult.Error(
                    DomainException.OrderNotFoundException(orderId)
                )
            }

            // Check if order can be cancelled (only PLACED orders can be cancelled)
            if (order.status != OrderStatus.PLACED && order.status != OrderStatus.ONGOING) {
                return DomainResult.Error(
                    DomainException.ValidationException(
                        message = "Only placed or ongoing orders can be cancelled"
                    )
                )
            }

            // Cancel the order
            orderRepository.cancelOrder(orderId)

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to cancel order: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

