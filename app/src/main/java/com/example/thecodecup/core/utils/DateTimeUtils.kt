package com.example.thecodecup.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for date/time formatting
 */
object DateTimeUtils {

    private val orderDateFormat = SimpleDateFormat("dd MMMM | hh:mm a", Locale.US)
    private val shortDateFormat = SimpleDateFormat("dd MMM", Locale.US)
    private val fullDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)

    /**
     * Format date for order display (e.g., "24 June | 12:30 PM")
     */
    fun formatOrderDate(date: Date): String {
        return orderDateFormat.format(date)
    }

    /**
     * Format date as short string (e.g., "24 Jun")
     */
    fun formatShortDate(date: Date): String {
        return shortDateFormat.format(date)
    }

    /**
     * Format date as full string (e.g., "24 June 2025")
     */
    fun formatFullDate(date: Date): String {
        return fullDateFormat.format(date)
    }

    /**
     * Get current date formatted for orders
     */
    fun getCurrentOrderDate(): String {
        return formatOrderDate(Date())
    }

    /**
     * Get greeting based on current time
     */
    fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}

