package com.example.thecodecup.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.presentation.components.colors.AppColors

/**
 * Loyalty Card View showing user's loyalty progress
 */
@Composable
fun LoyaltyCardView(
    currentPoints: Int,
    maxPoints: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LoyaltyCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Loyalty card", color = Color.White, fontSize = 14.sp)
                Text(text = "$currentPoints / $maxPoints", color = Color.White, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                    repeat(maxPoints) { index ->
                        LoyaltyCupIcon(isFilled = index < currentPoints)
                    }
                }
            }
        }
    }
}

@Composable
fun LoyaltyCupIcon(isFilled: Boolean) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = if (isFilled) AppColors.LoyaltyCard else Color.LightGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "☕", fontSize = 14.sp)
    }
}

