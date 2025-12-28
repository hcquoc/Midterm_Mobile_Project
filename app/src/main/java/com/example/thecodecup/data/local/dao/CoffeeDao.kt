package com.example.thecodecup.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thecodecup.data.local.entity.CoffeeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Coffee operations
 */
@Dao
interface CoffeeDao {

    /**
     * Insert a list of coffees, replacing on conflict
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coffees: List<CoffeeEntity>)

    /**
     * Insert a single coffee
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coffee: CoffeeEntity)

    /**
     * Get all coffees as Flow for reactive updates
     */
    @Query("SELECT * FROM coffees ORDER BY id ASC")
    fun getAllCoffees(): Flow<List<CoffeeEntity>>

    /**
     * Get all coffees (suspend version)
     */
    @Query("SELECT * FROM coffees ORDER BY id ASC")
    suspend fun getAllCoffeesList(): List<CoffeeEntity>

    /**
     * Get coffee by ID
     */
    @Query("SELECT * FROM coffees WHERE id = :id")
    suspend fun getCoffeeById(id: Int): CoffeeEntity?

    /**
     * Search coffees by name
     */
    @Query("SELECT * FROM coffees WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCoffees(query: String): Flow<List<CoffeeEntity>>

    /**
     * Get coffees by category
     */
    @Query("SELECT * FROM coffees WHERE category = :category ORDER BY name ASC")
    fun getCoffeesByCategory(category: String): Flow<List<CoffeeEntity>>

    /**
     * Delete all coffees
     */
    @Query("DELETE FROM coffees")
    suspend fun deleteAll()

    /**
     * Get coffee count
     */
    @Query("SELECT COUNT(*) FROM coffees")
    suspend fun getCount(): Int
}

