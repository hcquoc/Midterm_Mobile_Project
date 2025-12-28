package com.example.thecodecup.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeCategory
import com.example.thecodecup.presentation.components.colors.AppColors
import com.example.thecodecup.presentation.utils.PriceFormatter

/**
 * Professional Coffee Item Card for Home Screen
 * Features: Product image with add button, name, rating with stars, and price
 */
@Composable
fun CoffeeItemCard(
    coffee: Coffee,
    onCardClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = AppColors.Primary.copy(alpha = 0.1f)
            )
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Section with Add Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(AppColors.SurfaceContainer)
            ) {
                // Product Image
                if (imageResId > 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = coffee.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback - Coffee emoji
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "☕",
                            fontSize = 48.sp
                        )
                    }
                }

                // Add to Cart Button - Bottom Right Corner
                FilledIconButton(
                    onClick = onAddToCartClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = AppColors.Secondary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add to cart",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Coffee Name
                Text(
                    text = coffee.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating Row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = AppColors.Star,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = coffee.ratingDisplay,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                    if (coffee.reviewCount > 0) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = coffee.reviewCountDisplay,
                            fontSize = 11.sp,
                            color = AppColors.GrayText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price
                Text(
                    text = PriceFormatter.formatVND(coffee.basePrice),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoffeeItemCardPreview() {
    val sampleCoffee = Coffee(
        id = 1,
        name = "Cappuccino",
        basePrice = 45000.0,
        imageName = "coffee_cappuccino",
        description = "Espresso with steamed milk foam",
        category = CoffeeCategory.COFFEE,
        rating = 4.8,
        reviewCount = 245
    )

    CoffeeItemCard(
        coffee = sampleCoffee,
        onCardClick = {},
        onAddToCartClick = {},
        modifier = Modifier.width(160.dp)
    )
}

