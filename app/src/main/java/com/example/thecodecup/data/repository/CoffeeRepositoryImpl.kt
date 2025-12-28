package com.example.thecodecup.data.repository

import com.example.thecodecup.data.local.dao.CoffeeDao
import com.example.thecodecup.data.mapper.toDomain
import com.example.thecodecup.data.mapper.toDomainCoffees
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.repository.CoffeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of CoffeeRepository using Room Database
 */
class CoffeeRepositoryImpl(
    private val coffeeDao: CoffeeDao
) : CoffeeRepository {

    override fun getAllCoffees(): Flow<List<Coffee>> {
        return coffeeDao.getAllCoffees().map { entities ->
            entities.toDomainCoffees()
        }
    }

    override suspend fun getCoffeeById(id: Int): Coffee? {
        return coffeeDao.getCoffeeById(id)?.toDomain()
    }

    override fun searchCoffees(query: String): Flow<List<Coffee>> {
        return coffeeDao.searchCoffees(query).map { entities ->
            entities.toDomainCoffees()
        }
    }
}

