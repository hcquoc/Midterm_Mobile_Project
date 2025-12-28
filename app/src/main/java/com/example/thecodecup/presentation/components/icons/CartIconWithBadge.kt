package com.example.thecodecup.presentation.components.icons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.presentation.components.colors.AppColors

/**
 * Cart Icon with Badge showing item count
 */
@Composable
fun CartIconWithBadge(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable { onClick() }) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Cart",
            tint = AppColors.Primary,
            modifier = Modifier.size(24.dp)
        )

        if (itemCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-4).dp)
                    .size(16.dp)
                    .background(AppColors.Success, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (itemCount > 9) "9+" else itemCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

