package com.example.thecodecup.core.utils

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * Utility for generating unique IDs
 */
object IdGenerator {

    private val counter = AtomicInteger(0)

    /**
     * Generate a unique UUID string
     */
    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Generate a unique integer ID (incremental)
     */
    fun generateIntId(): Int {
        return counter.incrementAndGet()
    }

    /**
     * Generate a unique order ID with prefix
     */
    fun generateOrderId(): String {
        return "ORD-${System.currentTimeMillis()}-${counter.incrementAndGet()}"
    }

    /**
     * Generate a unique cart item ID
     */
    fun generateCartItemId(): Int {
        return generateIntId()
    }

    /**
     * Reset counter (useful for testing)
     */
    fun resetCounter() {
        counter.set(0)
    }
}

