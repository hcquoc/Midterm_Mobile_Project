package com.example.thecodecup.domain.usecase.coffee

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.repository.CoffeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Use case to get coffee menu list
 */
class GetCoffeeMenuUseCase(
    private val coffeeRepository: CoffeeRepository
) {

    /**
     * Execute the use case to get all coffees
     * @return Flow of DomainResult containing list of Coffee
     */
    operator fun invoke(): Flow<DomainResult<List<Coffee>>> {
        return coffeeRepository.getAllCoffees()
            .map<List<Coffee>, DomainResult<List<Coffee>>> { coffees ->
                DomainResult.Success(coffees)
            }
            .catch { exception ->
                emit(
                    DomainResult.Error(
                        DomainException.DatabaseException(
                            message = "Failed to load coffee menu: ${exception.message}",
                            cause = exception
                        )
                    )
                )
            }
    }
}

