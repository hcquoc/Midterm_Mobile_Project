package com.example.thecodecup.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.presentation.components.colors.AppColors

/**
 * Shared Loyalty Card Component used by both HomeScreen and RewardsScreen
 * This ensures consistent appearance and behavior across the app
 */
@Composable
fun SharedLoyaltyCard(
    currentStamps: Int,
    maxStamps: Int,
    onClick: () -> Unit = {},
    onRedeemClick: (() -> Unit)? = null,
    isRedeeming: Boolean = false,
    modifier: Modifier = Modifier
) {
    val canRedeem = currentStamps >= maxStamps

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LoyaltyCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Loyalty card",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "$currentStamps / $maxStamps",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stamps container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(maxStamps) { index ->
                        LoyaltyStampIcon(isFilled = index < currentStamps)
                    }
                }
            }

            // Redeem button (only show when stamps are full and callback is provided)
            if (canRedeem && onRedeemClick != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRedeemClick,
                    enabled = !isRedeeming,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Secondary,
                        disabledContainerColor = AppColors.Secondary.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (isRedeeming) "Redeeming..." else "Redeem Free Coffee",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun LoyaltyStampIcon(isFilled: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isFilled) AppColors.Secondary else AppColors.LightBackground
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "☕",
            fontSize = 16.sp,
            color = if (isFilled) Color.White else AppColors.GrayText
        )
    }
}

