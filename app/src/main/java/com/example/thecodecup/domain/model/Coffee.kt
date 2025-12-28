package com.example.thecodecup.domain.model

/**
 * Domain model for Coffee product
 */
data class Coffee(
    val id: Int,
    val name: String,
    val basePrice: Double = 35000.0, // VND - Vietnamese Dong
    val imageRes: Int? = null,
    val imageName: String? = null,
    val description: String = "",
    val category: CoffeeCategory = CoffeeCategory.COFFEE,
    val rating: Double = 4.5,
    val reviewCount: Int = 0
) {
    /**
     * Formatted rating display (e.g., "4.8")
     */
    val ratingDisplay: String
        get() = String.format("%.1f", rating)

    /**
     * Formatted review count (e.g., "120 reviews")
     */
    val reviewCountDisplay: String
        get() = if (reviewCount > 0) "($reviewCount)" else ""
}

/**
 * Coffee categories
 */
enum class CoffeeCategory {
    COFFEE,
    TEA,
    FREEZE
}

