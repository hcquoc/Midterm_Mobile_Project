package com.example.thecodecup.domain.model

/**
 * Coffee customization options
 *
 * Prices are in VND (whole numbers, no decimals)
 */
data class CoffeeOptions(
    val shot: Shot = Shot.SINGLE,
    val temperature: Temperature = Temperature.ICED,
    val size: Size = Size.MEDIUM,
    val ice: Ice = Ice.FULL
) {
    companion object {
        // Price constants in VND
        const val DOUBLE_SHOT_EXTRA: Long = 10000L      // +10,000đ
        const val LARGE_SIZE_EXTRA: Long = 10000L       // +10,000đ
        const val SMALL_SIZE_DISCOUNT: Long = 5000L     // -5,000đ
        const val ICED_EXTRA: Long = 5000L              // +5,000đ
    }

    /**
     * Calculate extra price based on options (in VND)
     * Uses Long internally, returns Double for compatibility
     */
    fun calculateExtraPrice(): Double {
        var extraLong = 0L

        // Shot pricing - Double shot +10,000đ
        if (shot == Shot.DOUBLE) {
            extraLong += DOUBLE_SHOT_EXTRA
        }

        // Size pricing
        when (size) {
            Size.SMALL -> extraLong -= SMALL_SIZE_DISCOUNT   // Small size -5,000đ
            Size.LARGE -> extraLong += LARGE_SIZE_EXTRA      // Large size +10,000đ
            Size.MEDIUM -> { /* no change */ }
        }

        // Temperature pricing - Iced +5,000đ
        if (temperature == Temperature.ICED) {
            extraLong += ICED_EXTRA
        }

        return extraLong.toDouble()
    }

    /**
     * Calculate extra price as Long (for precise calculations)
     */
    fun calculateExtraPriceLong(): Long {
        var extra = 0L

        if (shot == Shot.DOUBLE) {
            extra += DOUBLE_SHOT_EXTRA
        }

        when (size) {
            Size.SMALL -> extra -= SMALL_SIZE_DISCOUNT
            Size.LARGE -> extra += LARGE_SIZE_EXTRA
            Size.MEDIUM -> { /* no change */ }
        }

        if (temperature == Temperature.ICED) {
            extra += ICED_EXTRA
        }

        return extra
    }

    /**
     * Returns a formatted string of the customization details
     */
    fun toDisplayString(): String {
        return "${shot.displayName} | ${temperature.displayName} | ${size.displayName} | ${ice.displayName} ice"
    }
}

enum class Shot(val displayName: String) {
    SINGLE("Single"),
    DOUBLE("Double")
}

enum class Temperature(val displayName: String) {
    HOT("Hot"),
    ICED("Iced")
}

enum class Size(val displayName: String) {
    SMALL("S"),
    MEDIUM("M"),
    LARGE("L")
}

enum class Ice(val displayName: String) {
    LESS("Less"),
    NORMAL("Normal"),
    FULL("Full")
}

