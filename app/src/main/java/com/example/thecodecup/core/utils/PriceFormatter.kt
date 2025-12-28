package com.example.thecodecup.core.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility functions for price formatting
 */
object PriceFormatter {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    /**
     * Format price as currency string (e.g., "$3.00")
     */
    fun format(price: Double): String {
        return currencyFormat.format(price)
    }

    /**
     * Format price with simple dollar sign (e.g., "$3.00")
     */
    fun formatSimple(price: Double): String {
        return "$${String.format(Locale.US, "%.2f", price)}"
    }

    /**
     * Format price without currency symbol (e.g., "3.00")
     */
    fun formatWithoutSymbol(price: Double): String {
        return String.format(Locale.US, "%.2f", price)
    }
}

