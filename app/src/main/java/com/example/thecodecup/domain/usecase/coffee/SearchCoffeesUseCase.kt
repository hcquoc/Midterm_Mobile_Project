package com.example.thecodecup.domain.usecase.coffee

import com.example.thecodecup.domain.common.DomainException
import com.example.thecodecup.domain.common.DomainResult
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.repository.CoffeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Use case to search coffees by name
 */
class SearchCoffeesUseCase(
    private val coffeeRepository: CoffeeRepository
) {

    /**
     * Execute the use case to search coffees
     * @param query The search query
     * @return Flow of DomainResult containing list of matching Coffee
     */
    operator fun invoke(query: String): Flow<DomainResult<List<Coffee>>> {
        return coffeeRepository.searchCoffees(query)
            .map<List<Coffee>, DomainResult<List<Coffee>>> { coffees ->
                DomainResult.Success(coffees)
            }
            .catch { exception ->
                emit(
                    DomainResult.Error(
                        DomainException.DatabaseException(
                            message = "Failed to search coffees: ${exception.message}",
                            cause = exception
                        )
                    )
                )
            }
    }
}

