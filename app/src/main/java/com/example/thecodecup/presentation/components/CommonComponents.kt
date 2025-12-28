package com.example.thecodecup.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.presentation.components.colors.AppColors

// ═══════════════════════════════════════════════════════════════════════════
// PREMIUM COFFEE HOUSE - Common UI Components
// ═══════════════════════════════════════════════════════════════════════════

/**
 * AppPrimaryButton - Main CTA button with premium styling
 *
 * Features:
 * - Full width by default
 * - Rounded corners (12dp)
 * - Warm caramel color
 * - Subtle elevation
 * - Press animation with scale effect
 * - Loading state support
 */
@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    height: Dp = 56.dp,
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Secondary,
            contentColor = Color.White,
            disabledContainerColor = AppColors.Disabled,
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            pressedElevation = elevation + 2.dp,
            disabledElevation = 0.dp
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * AppSecondaryButton - Outlined button for secondary actions
 */
@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    height: Dp = 56.dp,
    cornerRadius: Dp = 12.dp
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AppColors.Primary,
            disabledContentColor = AppColors.Disabled
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (enabled) AppColors.Primary else AppColors.Disabled
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * AppIconButton - Standardized icon button with ripple effect
 *
 * Features:
 * - Consistent 48dp touch target
 * - Semi-transparent background option
 * - Premium ripple effect
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = AppColors.Primary,
    backgroundColor: Color = Color.Transparent,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else AppColors.Disabled,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * AppCard - Premium card wrapper with consistent styling
 *
 * Features:
 * - Subtle elevation (4dp default)
 * - Rounded corners (12dp)
 * - Warm white background
 * - Optional click ripple
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    elevation: Dp = 4.dp,
    backgroundColor: Color = AppColors.CardBackground,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * AppElevatedCard - Card with higher elevation for prominent content
 */
@Composable
fun AppElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        elevation = elevation,
        backgroundColor = AppColors.CardElevated,
        content = content
    )
}

/**
 * AppSurface - Surface container with warm cream background
 */
@Composable
fun AppSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(0.dp),
    color: Color = AppColors.Surface,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        shadowElevation = elevation,
        content = content
    )
}

/**
 * AppDivider - Warm-toned divider
 */
@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = AppColors.Divider
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

/**
 * PriceText - Styled price display
 */
@Composable
fun PriceText(
    price: Double,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = AppColors.PriceTag
) {
    Text(
        text = String.format(java.util.Locale.US, "$%.2f", price),
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color
    )
}

/**
 * RatingStars - Gold star rating display
 */
@Composable
fun RatingDisplay(
    rating: Double,
    modifier: Modifier = Modifier,
    reviewCount: Int? = null,
    starSize: Dp = 16.dp,
    textSize: TextUnit = 14.sp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = AppColors.Star,
            modifier = Modifier.size(starSize)
        )
        Text(
            text = String.format(java.util.Locale.US, "%.1f", rating),
            fontSize = textSize,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary
        )
        if (reviewCount != null) {
            Text(
                text = "($reviewCount)",
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
        }
    }
}

/**
 * SectionHeader - Consistent section title styling
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Primary
        )
        action?.invoke()
    }
}

/**
 * AppChip - Styled chip for filters/tags
 */
@Composable
fun AppChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) AppColors.Secondary else AppColors.SurfaceVariant
    val contentColor = if (selected) Color.White else AppColors.TextPrimary

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor
        )
    }
}

/**
 * GradientBackground - Warm gradient for headers
 */
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(AppColors.Primary, AppColors.PrimaryVariant),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(
            Brush.verticalGradient(colors = colors)
        ),
        content = content
    )
}

/**
 * LoadingOverlay - Full screen loading indicator
 */
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Scrim),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = AppColors.Secondary,
                strokeWidth = 3.dp
            )
        }
    }
}

/**
 * EmptyState - Consistent empty state display
 */
@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.TextTertiary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        if (action != null) {
            Spacer(modifier = Modifier.height(24.dp))
            action()
        }
    }
}

/**
 * BadgeBox - Notification badge wrapper
 */
@Composable
fun AppBadge(
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    BadgedBox(
        modifier = modifier,
        badge = {
            if (count > 0) {
                Badge(
                    containerColor = AppColors.Badge,
                    contentColor = Color.White
                ) {
                    Text(
                        text = if (count > 99) "99+" else count.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        content = content
    )
}

