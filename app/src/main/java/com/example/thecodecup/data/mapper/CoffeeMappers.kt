package com.example.thecodecup.data.mapper

import com.example.thecodecup.data.local.entity.CartItemEntity
import com.example.thecodecup.data.local.entity.CoffeeEntity
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeCategory
import com.example.thecodecup.domain.model.CoffeeOptions
import com.example.thecodecup.domain.model.Ice
import com.example.thecodecup.domain.model.Shot
import com.example.thecodecup.domain.model.Size
import com.example.thecodecup.domain.model.Temperature
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

// ==================== Gson instance for JSON conversion ====================
private val gson = Gson()

// ==================== Coffee Mappers ====================

/**
 * Convert CoffeeEntity to Domain Coffee
 */
fun CoffeeEntity.toDomain(): Coffee {
    return Coffee(
        id = id,
        name = name,
        basePrice = price,
        imageRes = null,
        imageName = imageUrl, // Map imageUrl field to imageName
        description = description,
        category = try {
            CoffeeCategory.valueOf(category)
        } catch (e: IllegalArgumentException) {
            CoffeeCategory.COFFEE
        },
        rating = rating,
        reviewCount = reviewCount
    )
}

/**
 * Convert Domain Coffee to CoffeeEntity
 */
fun Coffee.toEntity(): CoffeeEntity {
    return CoffeeEntity(
        id = id,
        name = name,
        price = basePrice,
        description = description,
        imageUrl = imageName, // Map imageName to imageUrl field
        category = category.name,
        rating = rating,
        reviewCount = reviewCount
    )
}

/**
 * Convert list of CoffeeEntity to list of Domain Coffee
 */
fun List<CoffeeEntity>.toDomainCoffees(): List<Coffee> = map { it.toDomain() }

/**
 * Convert list of Domain Coffee to list of CoffeeEntity
 */
fun List<Coffee>.toCoffeeEntities(): List<CoffeeEntity> = map { it.toEntity() }

// ==================== CoffeeOptions JSON Conversion ====================

/**
 * Data class for JSON serialization of CoffeeOptions
 */
private data class CoffeeOptionsJson(
    val shot: String = Shot.SINGLE.name,
    val temperature: String = Temperature.ICED.name,
    val size: String = Size.MEDIUM.name,
    val ice: String = Ice.FULL.name
)

/**
 * Convert CoffeeOptions to JSON String
 */
fun CoffeeOptions.toJsonString(): String {
    val json = CoffeeOptionsJson(
        shot = shot.name,
        temperature = temperature.name,
        size = size.name,
        ice = ice.name
    )
    return gson.toJson(json)
}

/**
 * Convert JSON String to CoffeeOptions
 */
fun String.toCoffeeOptions(): CoffeeOptions {
    return try {
        val json = gson.fromJson(this, CoffeeOptionsJson::class.java)
        CoffeeOptions(
            shot = try { Shot.valueOf(json.shot) } catch (e: Exception) { Shot.SINGLE },
            temperature = try { Temperature.valueOf(json.temperature) } catch (e: Exception) { Temperature.ICED },
            size = try { Size.valueOf(json.size) } catch (e: Exception) { Size.MEDIUM },
            ice = try { Ice.valueOf(json.ice) } catch (e: Exception) { Ice.FULL }
        )
    } catch (e: JsonSyntaxException) {
        CoffeeOptions() // Return default options on parse error
    }
}

// ==================== CartItem Mappers ====================

/**
 * Convert CartItemEntity to Domain CartItem
 * Note: Requires a Coffee object to be provided since entity only stores coffeeId
 */
fun CartItemEntity.toDomain(coffee: Coffee): CartItem {
    return CartItem(
        id = id,
        coffee = coffee,
        options = selectedOptions.toCoffeeOptions(),
        quantity = quantity
    )
}

/**
 * Convert CartItemEntity to Domain CartItem
 * Creates a minimal Coffee object from entity data when full Coffee is not available
 */
fun CartItemEntity.toDomainWithMinimalCoffee(): CartItem {
    val minimalCoffee = Coffee(
        id = coffeeId,
        name = coffeeName,
        basePrice = coffeeBasePrice
    )
    return CartItem(
        id = id,
        coffee = minimalCoffee,
        options = selectedOptions.toCoffeeOptions(),
        quantity = quantity
    )
}

/**
 * Convert Domain CartItem to CartItemEntity
 */
fun CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        id = id,
        coffeeId = coffee.id,
        coffeeName = coffee.name,
        coffeeBasePrice = coffee.basePrice,
        quantity = quantity,
        selectedOptions = options.toJsonString(),
        unitPrice = unitPrice
    )
}

/**
 * Convert list of CartItemEntity to list of Domain CartItem
 */
fun List<CartItemEntity>.toDomainCartItems(): List<CartItem> = map { it.toDomainWithMinimalCoffee() }

/**
 * Convert list of Domain CartItem to list of CartItemEntity
 */
fun List<CartItem>.toCartItemEntities(): List<CartItemEntity> = map { it.toEntity() }

