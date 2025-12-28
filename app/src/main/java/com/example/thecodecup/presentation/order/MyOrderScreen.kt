package com.example.thecodecup.presentation.order

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecodecup.domain.model.OrderStatus
import com.example.thecodecup.presentation.components.colors.AppColors

private const val TAG = "MyOrderScreen"

@Composable
fun MyOrderScreen(
    viewModel: MyOrdersViewModel = viewModel(),
    onHomeClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onOrderClick: (OrderDisplayItem) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = { MyOrderTopBar() },
        bottomBar = {
            com.example.thecodecup.presentation.components.bottomnav.AppBottomNavBar(
                selectedIndex = 2,
                onHomeClick = onHomeClick,
                onRewardsClick = onRewardsClick,
                onOrdersClick = onOrdersClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Custom Tab Row
            OrderTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.onEvent(MyOrdersUiEvent.SelectTab(it)) }
            )

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.Primary)
                }
            } else {
                // Orders List
                val orders = if (uiState.selectedTab == 0) {
                    uiState.ongoingDisplayItems
                } else {
                    uiState.historyDisplayItems
                }

                if (orders.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Receipt,
                                contentDescription = null,
                                tint = AppColors.GrayText,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (uiState.selectedTab == 0) "No ongoing orders" else "No order history",
                                fontSize = 16.sp,
                                color = AppColors.GrayText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(orders) { order ->
                            OrderItemCard(
                                order = order,
                                onClick = {
                                    Log.d(TAG, "Order clicked: ${order.id}")
                                    onOrderClick(order)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyOrderTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "My Order",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun OrderTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // On going tab
        OrderTab(
            title = "On going",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )

        // History tab
        OrderTab(
            title = "History",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun OrderTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) AppColors.Primary else AppColors.GrayText
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Indicator line
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(2.dp)
                .background(
                    if (isSelected) AppColors.Primary else Color.Transparent,
                    RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
fun OrderItemCard(
    order: OrderDisplayItem,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Date header with status chip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.date,
                fontSize = 12.sp,
                color = AppColors.GrayText
            )

            // Status Chip
            OrderStatusChip(status = order.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Order content row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Coffee icon
            Icon(
                imageVector = Icons.Default.LocalCafe,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Order details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = order.coffeeName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary
                )
            }

            // Price in VND format
            Text(
                text = order.formattedPrice,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Address row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = AppColors.GrayText,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = order.address,
                fontSize = 12.sp,
                color = AppColors.GrayText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        HorizontalDivider(
            color = AppColors.LightBackground,
            thickness = 1.dp
        )
    }
}

/**
 * Status chip with color coding
 * Orange for ongoing orders, Green for completed, Red for cancelled
 */
@Composable
fun OrderStatusChip(status: OrderStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        OrderStatus.PLACED -> Triple(
            Color(0xFFFFF3E0), // Light orange
            Color(0xFFE65100), // Dark orange
            "Placed"
        )
        OrderStatus.ONGOING -> Triple(
            Color(0xFFFFF3E0), // Light orange
            Color(0xFFE65100), // Dark orange
            "On Going"
        )
        OrderStatus.COMPLETED -> Triple(
            Color(0xFFE8F5E9), // Light green
            Color(0xFF2E7D32), // Dark green
            "Completed"
        )
        OrderStatus.CANCELLED -> Triple(
            Color(0xFFFFEBEE), // Light red
            Color(0xFFC62828), // Dark red
            "Cancelled"
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MyOrderBottomNav(
    selectedIndex: Int = 2,
    onHomeClick: () -> Unit,
    onRewardsClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItemOrder(
                icon = Icons.Outlined.Home,
                isSelected = selectedIndex == 0,
                onClick = onHomeClick
            )
            BottomNavItemOrder(
                icon = Icons.Outlined.CardGiftcard,
                isSelected = selectedIndex == 1,
                onClick = onRewardsClick
            )
            BottomNavItemOrder(
                icon = Icons.Outlined.Receipt,
                isSelected = selectedIndex == 2,
                onClick = onOrdersClick
            )
        }
    }
}

@Composable
fun BottomNavItemOrder(
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
            modifier = Modifier.size(26.dp)
        )
    }
}

// Sample data
val sampleOngoingOrders = listOf(
    OrderDisplayItem(
        id = "1",
        coffeeName = "Americano",
        date = "24 June | 12:30 PM",
        address = "3 Addersion Court Chino Hills, HO56824, United State",
        price = 35000.0,
        status = OrderStatus.ONGOING,
        formattedPrice = "35.000đ"
    ),
    OrderDisplayItem(
        id = "2",
        coffeeName = "Cafe Latte",
        date = "24 June | 12:30 PM",
        address = "3 Addersion Court Chino Hills, HO56824, United State",
        price = 45000.0,
        status = OrderStatus.PLACED,
        formattedPrice = "45.000đ"
    ),
    OrderDisplayItem(
        id = "3",
        coffeeName = "Flat White",
        date = "24 June | 12:30 PM",
        address = "3 Addersion Court Chino Hills, HO56824, United State",
        price = 45000.0,
        status = OrderStatus.ONGOING,
        formattedPrice = "45.000đ"
    )
)

val sampleHistoryOrders = listOf(
    OrderDisplayItem(
        id = "4",
        coffeeName = "Cappuccino",
        date = "20 June | 10:30 AM",
        address = "3 Addersion Court Chino Hills, HO56824, United State",
        price = 45000.0,
        status = OrderStatus.COMPLETED,
        formattedPrice = "45.000đ"
    ),
    OrderDisplayItem(
        id = "5",
        coffeeName = "Mocha",
        date = "18 June | 09:00 AM",
        address = "3 Addersion Court Chino Hills, HO56824, United State",
        price = 55000.0,
        status = OrderStatus.CANCELLED,
        formattedPrice = "55.000đ"
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyOrderScreenPreview() {
    MyOrderScreen()
}

