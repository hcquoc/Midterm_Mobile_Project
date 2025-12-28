package com.example.thecodecup.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.model.OrderStatus
import com.example.thecodecup.presentation.components.colors.AppColors
import com.example.thecodecup.presentation.utils.PriceFormatter

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileContent(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                is ProfileUiEvent.NavigateBack -> onBackClick()
                else -> viewModel.onEvent(event)
            }
        }
    )
}

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onEvent: (ProfileUiEvent) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ProfileTopBar(
                onBackClick = { onEvent(ProfileUiEvent.NavigateBack) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Profile Header with Avatar
            item {
                ProfileHeader(
                    name = uiState.fullName,
                    email = uiState.email,
                    initials = uiState.avatarInitials,
                    loyaltyTier = uiState.loyaltyTier,
                    rewardPoints = uiState.rewardPoints
                )
            }

            // Summary Chips
            item {
                ProfileSummaryChips(
                    totalSpent = uiState.formattedTotalSpent,
                    loyaltyTier = uiState.loyaltyTier,
                    ordersCount = uiState.completedOrdersCount
                )
            }

            // Profile Fields Section
            item {
                SectionHeader(title = "Personal Information")
            }

            item {
                ProfileFieldsSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            // Order History Section
            item {
                SectionHeader(title = "Order History")
            }

            if (uiState.isLoadingOrders) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.Primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            } else if (uiState.orderHistory.isEmpty()) {
                item {
                    EmptyOrderHistory()
                }
            } else {
                items(uiState.orderHistory.take(5)) { order ->
                    OrderHistoryItem(order = order)
                }

                if (uiState.orderHistory.size > 5) {
                    item {
                        Text(
                            text = "View all ${uiState.orderHistory.size} orders →",
                            fontSize = 14.sp,
                            color = AppColors.Secondary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                .clickable { /* Navigate to full history */ }
                        )
                    }
                }
            }

            // Log Out Button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                LogOutButton(
                    onClick = { onEvent(ProfileUiEvent.LogOut) }
                )
            }
        }
    }
}

@Composable
fun ProfileTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = AppColors.Primary,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Primary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Invisible spacer for centering
        Spacer(modifier = Modifier.size(24.dp))
    }
}

/**
 * Profile Header with Avatar, Name, and Email
 */
@Composable
fun ProfileHeader(
    name: String,
    email: String,
    initials: String,
    loyaltyTier: LoyaltyTier,
    rewardPoints: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Primary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(AppColors.Secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email
        Text(
            text = email,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Loyalty Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = getLoyaltyTierColor(loyaltyTier).copy(alpha = 0.2f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = getLoyaltyTierColor(loyaltyTier),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${loyaltyTier.displayName} Member",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = getLoyaltyTierColor(loyaltyTier)
                )
                Text(
                    text = "• $rewardPoints pts",
                    fontSize = 14.sp,
                    color = getLoyaltyTierColor(loyaltyTier).copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Summary chips showing stats
 */
@Composable
fun ProfileSummaryChips(
    totalSpent: String,
    loyaltyTier: LoyaltyTier,
    ordersCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryChip(
            icon = Icons.Outlined.ShoppingBag,
            label = "Orders",
            value = ordersCount.toString(),
            modifier = Modifier.weight(1f)
        )
        SummaryChip(
            icon = Icons.Outlined.Payments,
            label = "Total Spent",
            value = totalSpent,
            modifier = Modifier.weight(1f)
        )
        SummaryChip(
            icon = Icons.Outlined.CardGiftcard,
            label = "Tier",
            value = loyaltyTier.displayName,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryChip(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = AppColors.GrayText
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.Primary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

/**
 * Profile fields section with edit capability
 */
@Composable
fun ProfileFieldsSection(
    uiState: ProfileUiState,
    onEvent: (ProfileUiEvent) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        ProfileFieldItem(
            label = "Full name",
            value = uiState.fullName,
            icon = Icons.Outlined.Person,
            isEditing = uiState.editingField == ProfileField.FULL_NAME,
            editValue = if (uiState.editingField == ProfileField.FULL_NAME) uiState.editValue else uiState.fullName,
            onEditClick = { onEvent(ProfileUiEvent.StartEditing(ProfileField.FULL_NAME)) },
            onValueChange = { onEvent(ProfileUiEvent.UpdateEditValue(it)) },
            onSave = { onEvent(ProfileUiEvent.SaveEdit) },
            onCancel = { onEvent(ProfileUiEvent.CancelEdit) }
        )

        ProfileFieldItem(
            label = "Phone number",
            value = uiState.phoneNumber,
            icon = Icons.Outlined.Phone,
            isEditing = uiState.editingField == ProfileField.PHONE_NUMBER,
            editValue = if (uiState.editingField == ProfileField.PHONE_NUMBER) uiState.editValue else uiState.phoneNumber,
            onEditClick = { onEvent(ProfileUiEvent.StartEditing(ProfileField.PHONE_NUMBER)) },
            onValueChange = { onEvent(ProfileUiEvent.UpdateEditValue(it)) },
            onSave = { onEvent(ProfileUiEvent.SaveEdit) },
            onCancel = { onEvent(ProfileUiEvent.CancelEdit) }
        )

        ProfileFieldItem(
            label = "Email",
            value = uiState.email,
            icon = Icons.Outlined.Email,
            isEditing = uiState.editingField == ProfileField.EMAIL,
            editValue = if (uiState.editingField == ProfileField.EMAIL) uiState.editValue else uiState.email,
            onEditClick = { onEvent(ProfileUiEvent.StartEditing(ProfileField.EMAIL)) },
            onValueChange = { onEvent(ProfileUiEvent.UpdateEditValue(it)) },
            onSave = { onEvent(ProfileUiEvent.SaveEdit) },
            onCancel = { onEvent(ProfileUiEvent.CancelEdit) }
        )

        ProfileFieldItem(
            label = "Address",
            value = uiState.address,
            icon = Icons.Outlined.LocationOn,
            isEditing = uiState.editingField == ProfileField.ADDRESS,
            editValue = if (uiState.editingField == ProfileField.ADDRESS) uiState.editValue else uiState.address,
            onEditClick = { onEvent(ProfileUiEvent.StartEditing(ProfileField.ADDRESS)) },
            onValueChange = { onEvent(ProfileUiEvent.UpdateEditValue(it)) },
            onSave = { onEvent(ProfileUiEvent.SaveEdit) },
            onCancel = { onEvent(ProfileUiEvent.CancelEdit) },
            isMultiline = true
        )
    }
}

/**
 * Order History Item
 */
@Composable
fun OrderHistoryItem(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Order icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    // Order ID or items summary
                    Text(
                        text = "Order #${order.id.takeLast(6).uppercase()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Primary
                    )
                    // Date
                    Text(
                        text = order.formattedDate,
                        fontSize = 12.sp,
                        color = AppColors.GrayText
                    )
                    // Items count
                    Text(
                        text = "${order.items.size} item${if (order.items.size > 1) "s" else ""}",
                        fontSize = 12.sp,
                        color = AppColors.GrayText
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                // Price
                Text(
                    text = PriceFormatter.formatVND(order.totalPrice),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
                // Status chip
                OrderStatusChip(status = order.status)
            }
        }
    }
}

@Composable
fun OrderStatusChip(status: OrderStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        OrderStatus.COMPLETED -> Triple(
            AppColors.Success.copy(alpha = 0.1f),
            AppColors.Success,
            "Completed"
        )
        OrderStatus.ONGOING -> Triple(
            AppColors.Secondary.copy(alpha = 0.1f),
            AppColors.Secondary,
            "Ongoing"
        )
        OrderStatus.PLACED -> Triple(
            AppColors.Primary.copy(alpha = 0.1f),
            AppColors.Primary,
            "Placed"
        )
        OrderStatus.CANCELLED -> Triple(
            AppColors.Error.copy(alpha = 0.1f),
            AppColors.Error,
            "Cancelled"
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyOrderHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Receipt,
            contentDescription = null,
            tint = AppColors.GrayText.copy(alpha = 0.5f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No orders yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.GrayText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your order history will appear here",
            fontSize = 14.sp,
            color = AppColors.GrayText.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Log Out Button
 */
@Composable
fun LogOutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AppColors.Error
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = AppColors.Error.copy(alpha = 0.5f)
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Log Out",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Get color for loyalty tier
 */
@Composable
fun getLoyaltyTierColor(tier: LoyaltyTier): Color {
    return when (tier) {
        LoyaltyTier.BRONZE -> Color(0xFFCD7F32)
        LoyaltyTier.SILVER -> Color(0xFFC0C0C0)
        LoyaltyTier.GOLD -> Color(0xFFFFD700)
        LoyaltyTier.PLATINUM -> Color(0xFFE5E4E2)
    }
}

@Composable
fun ProfileFieldItem(
    label: String,
    value: String,
    icon: ImageVector,
    isEditing: Boolean,
    editValue: String,
    onEditClick: () -> Unit,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isMultiline: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = if (isMultiline && !isEditing) Alignment.Top else Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppColors.LightBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = AppColors.GrayText
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = !isMultiline,
                        maxLines = if (isMultiline) 3 else 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Primary,
                            unfocusedBorderColor = AppColors.Divider,
                            cursorColor = AppColors.Primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onCancel) {
                            Text(
                                text = "Cancel",
                                color = AppColors.GrayText
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onSave,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Secondary
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Save",
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Primary,
                        lineHeight = 20.sp
                    )
                }
            }

            // Edit icon (only show when not editing)
            if (!isEditing) {
                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit $label",
                    tint = AppColors.Primary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEditClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileContent(
        uiState = ProfileUiState(
            orderHistory = listOf(
                Order(
                    id = "ORD123456",
                    items = emptyList(),
                    totalPrice = 12.50,
                    status = OrderStatus.COMPLETED,
                    address = "123 Main St"
                )
            )
        ),
        onEvent = {}
    )
}
