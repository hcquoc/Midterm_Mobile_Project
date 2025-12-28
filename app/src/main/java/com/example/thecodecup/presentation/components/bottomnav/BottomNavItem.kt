package com.example.thecodecup.presentation.components.bottomnav

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.example.thecodecup.presentation.components.colors.AppColors

/**
 * Bottom Navigation Item
 */
@Composable
fun BottomNavItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) AppColors.Primary else AppColors.GrayText,
            modifier = Modifier.size(28.dp)
        )
    }
}

