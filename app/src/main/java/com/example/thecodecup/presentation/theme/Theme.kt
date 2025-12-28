package com.example.thecodecup.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ==================== Premium Coffee Shop Shapes ====================
// Consistent rounded corners throughout the app
val CoffeeShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),    // Chips, small badges
    small = RoundedCornerShape(8.dp),          // Buttons, small cards
    medium = RoundedCornerShape(12.dp),        // Standard cards, dialogs
    large = RoundedCornerShape(16.dp),         // Large cards, bottom sheets
    extraLarge = RoundedCornerShape(28.dp)     // FAB, special elements
)

// ==================== Light Color Scheme ====================
private val CoffeeLightColorScheme = lightColorScheme(
    // Primary - Deep Espresso
    primary = EspressoPrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = EspressoContainer,
    onPrimaryContainer = EspressoPrimary,

    // Secondary - Warm Caramel (CTA buttons, accents)
    secondary = CoffeeCaramel,
    onSecondary = TextOnSecondary,
    secondaryContainer = CoffeeCaramelLight,
    onSecondaryContainer = CoffeeCaramelDark,

    // Tertiary - Gold Accent (stars, highlights)
    tertiary = CoffeeGoldAccent,
    onTertiary = EspressoPrimary,
    tertiaryContainer = CoffeeGoldAccent.copy(alpha = 0.2f),
    onTertiaryContainer = CoffeeAmber,

    // Background - Soft Cream (NOT stark white)
    background = CreamBackground,
    onBackground = TextPrimary,

    // Surface - Slightly warm white
    surface = CreamSurface,
    onSurface = TextOnSurface,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = TextOnSurfaceVariant,
    surfaceContainerLowest = CreamSurfaceElevated,
    surfaceContainerLow = CreamContainer,
    surfaceContainer = CreamContainerHigh,
    surfaceContainerHigh = CreamSurfaceVariant,
    surfaceContainerHighest = CreamSurfaceVariant,

    // Status colors
    error = StatusError,
    onError = Color.White,
    errorContainer = StatusErrorLight,
    onErrorContainer = StatusError,

    // Outlines & Dividers
    outline = OutlineColor,
    outlineVariant = DividerColor,

    // Inverse colors
    inverseSurface = EspressoPrimary,
    inverseOnSurface = TextOnPrimary,
    inversePrimary = CoffeeCaramelLight,

    // Scrim
    scrim = ScrimColor
)

// ==================== Dark Color Scheme ====================
private val CoffeeDarkColorScheme = darkColorScheme(
    primary = CoffeeCaramel,
    onPrimary = EspressoDark,
    primaryContainer = EspressoPrimaryVariant,
    onPrimaryContainer = TextOnPrimary,

    secondary = CoffeeCaramelLight,
    onSecondary = EspressoDark,
    secondaryContainer = CoffeeCaramelDark,
    onSecondaryContainer = TextOnSecondary,

    tertiary = CoffeeGoldAccent,
    onTertiary = EspressoDark,

    background = EspressoDark,
    onBackground = TextOnPrimary,

    surface = EspressoPrimaryVariant,
    onSurface = TextOnPrimary,
    surfaceVariant = EspressoLight,
    onSurfaceVariant = TextSecondary,

    error = StatusError,
    onError = Color.White,

    outline = EspressoLight,
    outlineVariant = EspressoLight
)

@Composable
fun TheCodeCupTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CoffeeDarkColorScheme else CoffeeLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = CoffeeShapes,
        content = content
    )
}