package com.example.thecodecup.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.presentation.theme.TheCodeCupTheme
import com.example.thecodecup.presentation.components.colors.AppColors
import com.example.thecodecup.presentation.utils.PriceFormatter

// ═══════════════════════════════════════════════════════════════════════════
// PREMIUM COFFEE HOUSE - Home Screen
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun HomeScreen(
    onCoffeeClick: (Coffee) -> Unit = {},
    onAddToCartClick: (Coffee) -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    cartItemCount: Int = 0,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppColors.Primary,
                        AppColors.PrimaryVariant
                    )
                )
            )
    ) {
        // Elegant Header Section
        PremiumHeaderSection(
            userName = uiState.userName,
            greeting = uiState.greeting,
            cartItemCount = cartItemCount,
            onCartClick = onCartClick,
            onProfileClick = onProfileClick
        )

        // Loyalty Card Section
        LoyaltyCardSection(
            currentPoints = uiState.loyaltyStamps,
            maxPoints = uiState.maxLoyaltyStamps
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Coffee Selection Section (Scrollable)
        PremiumCoffeeSelectionSection(
            coffeeList = uiState.coffeeList,
            onCoffeeClick = onCoffeeClick,
            onAddToCartClick = onAddToCartClick,
            modifier = Modifier.weight(1f)
        )

        // Bottom Navigation
        com.example.thecodecup.presentation.components.bottomnav.AppBottomNavBar(
            selectedIndex = 0,
            onHomeClick = {},
            onRewardsClick = onRewardsClick,
            onOrdersClick = onOrdersClick
        )
    }
}

/**
 * Premium Header with elegant greeting and action buttons
 */
@Composable
fun PremiumHeaderSection(
    userName: String,
    greeting: String = "Good morning",
    cartItemCount: Int = 0,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Greeting Column
        Column {
            Text(
                text = greeting,
                color = AppColors.SecondaryLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userName,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cart Button with Badge
            PremiumIconButton(
                icon = Icons.Outlined.ShoppingCart,
                contentDescription = "Cart",
                onClick = onCartClick,
                badgeCount = cartItemCount
            )

            // Profile Button
            PremiumIconButton(
                icon = Icons.Outlined.Person,
                contentDescription = "Profile",
                onClick = onProfileClick
            )
        }
    }
}

/**
 * Premium Icon Button with optional badge
 */
@Composable
fun PremiumIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    badgeCount: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }

    BadgedBox(
        badge = {
            if (badgeCount > 0) {
                Badge(
                    containerColor = AppColors.Badge,
                    contentColor = Color.White
                ) {
                    Text(
                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = Color.White),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LoyaltyCardSection(
    currentPoints: Int,
    maxPoints: Int
) {
    com.example.thecodecup.presentation.components.cards.SharedLoyaltyCard(
        currentStamps = currentPoints,
        maxStamps = maxPoints,
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}

/**
 * Premium Coffee Selection with polished cards
 */
@Composable
fun PremiumCoffeeSelectionSection(
    coffeeList: List<Coffee>,
    onCoffeeClick: (Coffee) -> Unit,
    onAddToCartClick: (Coffee) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = AppColors.Background,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            // Section Header
            Text(
                text = "Choose your coffee",
                color = AppColors.Primary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
            )

            // Coffee Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(coffeeList) { coffee ->
                    PremiumCoffeeCard(
                        coffee = coffee,
                        onClick = { onCoffeeClick(coffee) },
                        onAddToCartClick = { onAddToCartClick(coffee) }
                    )
                }
            }
        }
    }
}

/**
 * Premium Coffee Card with image, name, price, and add button
 */
@Composable
fun PremiumCoffeeCard(
    coffee: Coffee,
    onClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(coffee.imageName) {
        if (coffee.imageName.isNullOrBlank()) {
            0
        } else {
            try {
                context.resources.getIdentifier(
                    coffee.imageName,
                    "drawable",
                    context.packageName
                )
            } catch (e: Exception) {
                0
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = AppColors.Primary.copy(alpha = 0.1f),
                spotColor = AppColors.Primary.copy(alpha = 0.1f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Coffee Image Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageResId > 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = coffee.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback placeholder
                        Text(
                            text = "☕",
                            fontSize = 56.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Coffee Name
                Text(
                    text = coffee.name,
                    color = AppColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price
                Text(
                    text = PriceFormatter.formatVND(coffee.basePrice),
                    color = AppColors.PriceTag,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Floating Add Button (bottom-right of image)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp + (LocalContext.current.resources.displayMetrics.density * 80).dp.coerceAtMost(120.dp), end = 8.dp)
            )

            // Add to Cart Button
            FloatingActionButton(
                onClick = onAddToCartClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 60.dp)
                    .size(36.dp),
                shape = CircleShape,
                containerColor = AppColors.Secondary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add to cart",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// LEGACY COMPONENTS (kept for backward compatibility)
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun HeaderSection(
    userName: String,
    greeting: String = "Good morning",
    cartItemCount: Int = 0,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    PremiumHeaderSection(
        userName = userName,
        greeting = greeting,
        cartItemCount = cartItemCount,
        onCartClick = onCartClick,
        onProfileClick = onProfileClick
    )
}

@Composable
fun CoffeeSelectionSection(
    coffeeList: List<Coffee>,
    onCoffeeClick: (Coffee) -> Unit,
    onAddToCartClick: (Coffee) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PremiumCoffeeSelectionSection(
        coffeeList = coffeeList,
        onCoffeeClick = onCoffeeClick,
        onAddToCartClick = onAddToCartClick,
        modifier = modifier
    )
}

@Composable
fun LoyaltyCupIcon(isFilled: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(
                color = if (isFilled) AppColors.Secondary else AppColors.SurfaceVariant,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "☕",
            fontSize = 18.sp,
            color = if (isFilled) Color.White else AppColors.TextTertiary
        )
    }
}

@Composable
fun CoffeeCard(
    coffee: Coffee,
    onClick: () -> Unit
) {
    PremiumCoffeeCard(
        coffee = coffee,
        onClick = onClick,
        onAddToCartClick = {}
    )
}

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Outlined.ShoppingCart,
                isSelected = true,
                onClick = onHomeClick
            )
            BottomNavItem(
                icon = Icons.Outlined.CardGiftcard,
                isSelected = false,
                onClick = onRewardsClick
            )
            BottomNavItem(
                icon = Icons.Outlined.Receipt,
                isSelected = false,
                onClick = onOrdersClick
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AppColors.Secondary else AppColors.TextTertiary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    TheCodeCupTheme {
        HomeScreen()
    }
}
