package com.example.thecodecup.presentation.order

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.presentation.components.colors.AppColors

@Composable
fun OrderSuccessScreen(
    orderId: String? = null,
    onTrackOrder: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.8f))

        // Animated Green Checkmark
        GreenCheckmarkAnimation()

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Order Placed Successfully!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Order ID if available
        if (!orderId.isNullOrEmpty()) {
            Text(
                text = "Order ID: $orderId",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.GrayText,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Description
        Text(
            text = "Your order has been placed successfully.\nFor more details, go to my orders.",
            fontSize = 14.sp,
            color = AppColors.GrayText,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Track My Order Button
        Button(
            onClick = onTrackOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Secondary),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
            Text(
                text = "Track My Order",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * Animated green checkmark with scale and fade animation
 */
@Composable
fun GreenCheckmarkAnimation() {
    // Scale animation
    val infiniteTransition = rememberInfiniteTransition(label = "checkmark")

    // Initial pop-in animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Subtle pulse animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(140.dp)
            .scale(scale * pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
        )

        // Middle ring
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50).copy(alpha = 0.25f))
        )

        // Inner circle with checkmark
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun OrderSuccessIcon() {
    // Custom drawn coffee bag icon similar to the design
    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer bag shape
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(y = 10.dp)
        ) {
            // Bag body
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ))
                    .background(Color.Transparent)
            ) {
                // Draw the bag outline
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Handle part
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(25.dp)
                            .offset(y = (-5).dp)
                    ) {
                        // Left handle
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(8.dp)
                                .height(25.dp)
                                .offset(x = 8.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(AppColors.Primary)
                        )
                        // Right handle
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(8.dp)
                                .height(25.dp)
                                .offset(x = (-8).dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(AppColors.Primary)
                        )
                        // Top connector
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .width(44.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(AppColors.Primary)
                        )
                    }

                    // Bag body outline
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .height(85.dp)
                            .clip(RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                                bottomStart = 12.dp,
                                bottomEnd = 12.dp
                            ))
                            .background(Color.Transparent)
                    ) {
                        // Left border
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(AppColors.Primary)
                        )
                        // Right border
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(AppColors.Primary)
                        )
                        // Bottom border
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                                .background(AppColors.Primary)
                        )
                        // Top border
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(AppColors.Primary)
                        )

                        // Coffee cup inside the bag
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Cup
                            CoffeeCupIcon()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoffeeCupIcon() {
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cup lid
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(AppColors.Primary)
            )

            // Cup body with lines
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(35.dp)
            ) {
                // Cup outline
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                        .background(Color.Transparent)
                ) {
                    // Left border
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(AppColors.Primary)
                    )
                    // Right border
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(AppColors.Primary)
                    )
                    // Bottom
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(28.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                            .background(AppColors.Primary)
                    )

                    // Horizontal lines inside cup
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(AppColors.Primary)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderSuccessScreenPreview() {
    OrderSuccessScreen()
}

