package com.example.thecodecup.presentation.components.colors

import androidx.compose.ui.graphics.Color

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * PREMIUM COFFEE HOUSE - Centralized App Colors
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * A unified color object that provides consistent colors across all components.
 * Uses the sophisticated Coffee House palette for a premium feel.
 */
object AppColors {

    // ═══════════════════════════════════════════════════════════════════════
    // PRIMARY BRAND COLORS - Deep Espresso Brown
    // ═══════════════════════════════════════════════════════════════════════
    val Primary = Color(0xFF3E2723)                // Deep espresso - main brand
    val PrimaryVariant = Color(0xFF4E342E)         // Slightly lighter espresso
    val PrimaryDark = Color(0xFF1B0000)            // Darkest roast
    val PrimaryLight = Color(0xFF6D4C41)           // Lighter brown
    val PrimaryContainer = Color(0xFFD7CCC8)       // Light brown container

    // ═══════════════════════════════════════════════════════════════════════
    // SECONDARY/ACCENT - Warm Caramel (CTA Buttons, Highlights)
    // ═══════════════════════════════════════════════════════════════════════
    val Secondary = Color(0xFFC67C4E)              // Warm caramel - main CTA
    val SecondaryDark = Color(0xFFA65E32)          // Darker caramel
    val SecondaryLight = Color(0xFFE8A87C)         // Light caramel
    val SecondaryContainer = Color(0xFFFFE0B2)     // Cream container

    // ═══════════════════════════════════════════════════════════════════════
    // ACCENT COLORS - Gold for Stars & Ratings
    // ═══════════════════════════════════════════════════════════════════════
    val Gold = Color(0xFFFFCA28)                   // Warm gold accent
    val GoldLight = Color(0xFFFFE57F)              // Light gold
    val Star = Color(0xFFFFB300)                   // Star rating gold
    val Amber = Color(0xFFFFA726)                  // Amber accent

    // ═══════════════════════════════════════════════════════════════════════
    // BACKGROUND & SURFACE - Soft Cream Tones (NOT stark white)
    // ═══════════════════════════════════════════════════════════════════════
    val Background = Color(0xFFFFF8E1)             // Warm cream background
    val Surface = Color(0xFFFFFBF5)                // Off-white surface
    val SurfaceElevated = Color(0xFFFFFFFF)        // White for elevated cards
    val SurfaceVariant = Color(0xFFF5F0EB)         // Warm gray-cream
    val SurfaceContainer = Color(0xFFFAF8F5)       // Container background
    val LightBackground = Color(0xFFF5F0EB)        // Light warm background

    // ═══════════════════════════════════════════════════════════════════════
    // TEXT COLORS - High Contrast, Readable
    // ═══════════════════════════════════════════════════════════════════════
    val TextPrimary = Color(0xFF2D2D2D)            // Dark gray (not black)
    val TextSecondary = Color(0xFF6B6B6B)          // Medium gray
    val GrayText = Color(0xFF6B6B6B)               // Alias for TextSecondary
    val TextTertiary = Color(0xFF9E9E9E)           // Light gray
    val TextHint = Color(0xFFBDBDBD)               // Hint/placeholder
    val OnPrimary = Color(0xFFFFFFFF)              // White on primary
    val OnSecondary = Color(0xFFFFFFFF)            // White on secondary
    val OnSurface = Color(0xFF2D2D2D)              // Text on surface

    // ═══════════════════════════════════════════════════════════════════════
    // STATUS COLORS
    // ═══════════════════════════════════════════════════════════════════════
    val Success = Color(0xFF388E3C)                // Forest green
    val SuccessLight = Color(0xFFE8F5E9)           // Success background
    val Error = Color(0xFFD32F2F)                  // Soft red
    val ErrorLight = Color(0xFFFFEBEE)             // Error background
    val Warning = Color(0xFFF57C00)                // Warm orange
    val WarningLight = Color(0xFFFFF3E0)           // Warning background
    val Info = Color(0xFF1976D2)                   // Info blue
    val InfoLight = Color(0xFFE3F2FD)              // Info background

    // ═══════════════════════════════════════════════════════════════════════
    // UI ELEMENTS
    // ═══════════════════════════════════════════════════════════════════════
    val Divider = Color(0xFFE8E0D8)                // Warm divider
    val Outline = Color(0xFFE0D6CC)                // Warm outline
    val CardBackground = Color(0xFFFFFFFF)         // Card background
    val CardElevated = Color(0xFFFFFBF7)           // Slightly warm card
    val Disabled = Color(0xFFBDBDBD)               // Disabled state
    val Ripple = Color(0x1A3E2723)                 // Brown ripple
    val Scrim = Color(0x80000000)                  // Overlay scrim

    // ═══════════════════════════════════════════════════════════════════════
    // SPECIAL UI ELEMENTS
    // ═══════════════════════════════════════════════════════════════════════
    val PriceTag = Color(0xFFC67C4E)               // Price display
    val Badge = Color(0xFFE53935)                  // Notification badge
    val ProgressTrack = Color(0xFFE0D6CC)          // Progress background
    val ProgressFill = Color(0xFFC67C4E)           // Progress indicator

    // ═══════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════
    val NavBackground = Color(0xFF3E2723)          // Nav bar background
    val NavSelected = Color(0xFFC67C4E)            // Selected item
    val NavUnselected = Color(0xFFBCAAA4)          // Unselected item
    val NavIndicator = Color(0x33C67C4E)           // Selection indicator

    // ═══════════════════════════════════════════════════════════════════════
    // LOYALTY & REWARDS
    // ═══════════════════════════════════════════════════════════════════════
    val LoyaltyCard = Color(0xFF4E342E)            // Loyalty card background
    val LoyaltyStampActive = Color(0xFFC67C4E)     // Active stamp
    val LoyaltyStampInactive = Color(0xFFBCAAA4)   // Inactive stamp
    val TierGold = Color(0xFFFFD700)               // Gold tier
    val TierSilver = Color(0xFFC0C0C0)             // Silver tier
    val TierBronze = Color(0xFFCD7F32)             // Bronze tier
    val TierPlatinum = Color(0xFFE5E4E2)           // Platinum tier

    // ═══════════════════════════════════════════════════════════════════════
    // SHIMMER & LOADING
    // ═══════════════════════════════════════════════════════════════════════
    val ShimmerBase = Color(0xFFE8E0D8)            // Shimmer base
    val ShimmerHighlight = Color(0xFFF5F0EB)       // Shimmer highlight
}
