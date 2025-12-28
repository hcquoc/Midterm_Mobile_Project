package com.example.thecodecup.presentation.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.roundToLong

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * PRICE FORMATTER & CALCULATOR UTILITIES
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Format utilities for Vietnamese currency (VND).
 * VND has no decimals, so all calculations should use Long to avoid
 * floating-point precision errors.
 */
object PriceFormatter {

    private val vnDecimalFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols.getInstance(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        DecimalFormat("#,###", symbols)
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FORMATTING FUNCTIONS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Format price to VND string with đ suffix
     * Example: 45000.0 -> "45.000đ"
     *
     * @param price Price in VND (Double)
     * @return Formatted string like "45.000đ"
     */
    fun formatVND(price: Double): String {
        val rounded = roundToVND(price)
        return "${vnDecimalFormat.format(rounded)}đ"
    }

    /**
     * Format price to VND string with space and đ suffix
     * Example: 45000.0 -> "45.000 đ"
     *
     * @param price Price in VND (Double)
     * @return Formatted string like "45.000 đ"
     */
    fun formatVNDSpaced(price: Double): String {
        val rounded = roundToVND(price)
        return "${vnDecimalFormat.format(rounded)} đ"
    }

    /**
     * Format price to VND string with full suffix
     * Example: 45000.0 -> "45.000 VNĐ"
     *
     * @param price Price in VND (Double)
     * @return Formatted string like "45.000 VNĐ"
     */
    fun formatVNDFull(price: Double): String {
        val rounded = roundToVND(price)
        return "${vnDecimalFormat.format(rounded)} VNĐ"
    }

    /**
     * Format price to short VND string for large amounts
     * Example: 150000.0 -> "150K"
     *
     * @param price Price in VND (Double)
     * @return Short formatted string like "150K" or "1.5M"
     */
    fun formatVNDShort(price: Double): String {
        val rounded = roundToVND(price)
        return when {
            rounded >= 1_000_000 -> {
                val millions = rounded / 1_000_000.0
                if (millions == millions.toLong().toDouble()) {
                    "${millions.toLong()}M"
                } else {
                    "${String.format(Locale.US, "%.1f", millions)}M"
                }
            }
            rounded >= 1_000 -> "${rounded / 1000}K"
            else -> "${rounded}đ"
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CALCULATION FUNCTIONS (Precision-safe for VND)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Round a Double price to Long (VND has no decimals)
     * Uses Math.round to avoid floating-point errors
     *
     * @param price Price as Double
     * @return Rounded price as Long
     */
    fun roundToVND(price: Double): Long {
        return price.roundToLong()
    }

    /**
     * Calculate item total with precision
     * Formula: (basePrice + extraPrice) * quantity
     *
     * @param basePrice Base price of item
     * @param extraPrice Extra price from options
     * @param quantity Number of items
     * @return Total price as Long (rounded)
     */
    fun calculateItemTotal(basePrice: Double, extraPrice: Double, quantity: Int): Long {
        val unitPrice = roundToVND(basePrice) + roundToVND(extraPrice)
        return unitPrice * quantity
    }

    /**
     * Calculate cart subtotal from list of item totals
     *
     * @param itemTotals List of individual item totals (as Long)
     * @return Sum of all item totals
     */
    fun calculateSubtotal(itemTotals: List<Long>): Long {
        return itemTotals.sum()
    }

    /**
     * Calculate final total with delivery fee
     *
     * @param subtotal Subtotal of all items
     * @param deliveryFee Delivery fee
     * @return Final total
     */
    fun calculateTotal(subtotal: Long, deliveryFee: Long): Long {
        return subtotal + deliveryFee
    }

    /**
     * Convert Long back to Double for compatibility
     *
     * @param amount Amount as Long
     * @return Amount as Double
     */
    fun toDouble(amount: Long): Double {
        return amount.toDouble()
    }
}
