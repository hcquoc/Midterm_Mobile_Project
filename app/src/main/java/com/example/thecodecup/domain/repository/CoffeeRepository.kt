package com.example.thecodecup.domain.repository

import com.example.thecodecup.domain.model.Coffee
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Coffee operations
 */
interface CoffeeRepository {

    /**
     * Get all available coffees
     */
    fun getAllCoffees(): Flow<List<Coffee>>

    /**
     * Get a coffee by ID
     */
    suspend fun getCoffeeById(id: Int): Coffee?

    /**
     * Search coffees by name
     */
    fun searchCoffees(query: String): Flow<List<Coffee>>
}

