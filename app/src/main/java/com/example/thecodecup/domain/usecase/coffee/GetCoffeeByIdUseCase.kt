package com.example.thecodecup.domain.usecase.coffee

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.repository.CoffeeRepository

/**
 * Use case to get a coffee by its ID
 */
class GetCoffeeByIdUseCase(
    private val coffeeRepository: CoffeeRepository
) {

    /**
     * Execute the use case to get a coffee by ID
     * @param coffeeId The ID of the coffee to retrieve
     * @return DomainResult containing Coffee or error
     */
    suspend operator fun invoke(coffeeId: Int): DomainResult<Coffee> {
        return try {
            val coffee = coffeeRepository.getCoffeeById(coffeeId)
            if (coffee != null) {
                DomainResult.Success(coffee)
            } else {
                DomainResult.Error(
                    DomainException.CoffeeNotFoundException(coffeeId)
                )
            }
        } catch (e: Exception) {
            DomainResult.Error(
                DomainException.DatabaseException(
                    message = "Failed to get coffee: ${e.message}",
                    cause = e
                )
            )
        }
    }
}

