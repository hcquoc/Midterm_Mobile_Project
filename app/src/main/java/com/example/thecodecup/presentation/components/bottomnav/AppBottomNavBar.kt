package com.example.thecodecup.presentation.components.bottomnav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.presentation.components.colors.AppColors

/**
 * Modern App Bottom Navigation Bar with floating design
 */
@Composable
fun AppBottomNavBar(
    selectedIndex: Int = 0,
    onHomeClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = AppColors.Primary,
        shadowElevation = 6.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModernNavItem(
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                label = "Home",
                isSelected = selectedIndex == 0,
                onClick = onHomeClick
            )
            ModernNavItem(
                selectedIcon = Icons.Filled.CardGiftcard,
                unselectedIcon = Icons.Outlined.CardGiftcard,
                label = "Rewards",
                isSelected = selectedIndex == 1,
                onClick = onRewardsClick
            )
            ModernNavItem(
                selectedIcon = Icons.Filled.Receipt,
                unselectedIcon = Icons.Outlined.Receipt,
                label = "Orders",
                isSelected = selectedIndex == 2,
                onClick = onOrdersClick
            )
        }
    }
}

@Composable
private fun ModernNavItem(
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isSelected) AppColors.Secondary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) AppColors.Secondary else Color.White.copy(alpha = 0.6f)
        )
    }
}
