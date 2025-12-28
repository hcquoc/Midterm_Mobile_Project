package com.example.thecodecup.presentation.rewards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.domain.model.RewardHistory
import com.example.thecodecup.domain.model.RewardType
import com.example.thecodecup.presentation.components.colors.AppColors

data class RewardHistoryItem(
    val id: Int,
    val name: String,
    val points: Int,
    val date: String
)

@Composable
fun RewardsScreen(
    onHomeClick: () -> Unit = {},
    onRewardsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onRedeemClick: () -> Unit = {},
    onLoyaltyCardClick: () -> Unit = {},
    viewModel: RewardsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = RewardsViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle redeem success with reward name
    LaunchedEffect(uiState.redeemSuccess) {
        if (uiState.redeemSuccess) {
            val rewardName = uiState.redeemedRewardName ?: "phần thưởng"
            snackbarHostState.showSnackbar(
                message = "🎉 Đổi thưởng thành công! Thưởng thức $rewardName của bạn!",
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(RewardsUiEvent.ConsumeRedeemSuccess)
        }
    }

    // Handle stamps redeem success
    LaunchedEffect(uiState.redeemStampsSuccess) {
        if (uiState.redeemStampsSuccess) {
            snackbarHostState.showSnackbar(
                message = "🎉 Đổi tem thành công! Thưởng thức ly cà phê miễn phí!",
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(RewardsUiEvent.ConsumeRedeemStampsSuccess)
        }
    }

    // Handle redeem error
    LaunchedEffect(uiState.redeemError) {
        uiState.redeemError?.let { error ->
            snackbarHostState.showSnackbar(
                message = "❌ $error",
                duration = SnackbarDuration.Short
            )
            viewModel.onEvent(RewardsUiEvent.ConsumeRedeemError)
        }
    }

    // Redeem Confirmation Dialog
    if (uiState.showRedeemDialog && uiState.selectedReward != null) {
        RedeemConfirmationDialog(
            reward = uiState.selectedReward!!,
            currentPoints = uiState.rewardPoints,
            isRedeeming = uiState.isRedeeming,
            onConfirm = { viewModel.onEvent(RewardsUiEvent.ConfirmRedemption) },
            onDismiss = { viewModel.onEvent(RewardsUiEvent.CancelRedemption) }
        )
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (uiState.redeemSuccess || uiState.redeemStampsSuccess) AppColors.Success else AppColors.Error,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = { RewardsTopBar() },
        bottomBar = {
            com.example.thecodecup.presentation.components.bottomnav.AppBottomNavBar(
                selectedIndex = 1,
                onHomeClick = onHomeClick,
                onRewardsClick = onRewardsClick,
                onOrdersClick = onOrdersClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Large Circular Points Display
            item {
                PointsProgressCard(
                    currentPoints = uiState.rewardPoints,
                    maxPoints = uiState.maxRewardPoints,
                    progress = uiState.rewardPointsProgress
                )
            }

            // Loyalty Card with redeem button
            item {
                LoyaltyCardWithRedeem(
                    currentStamps = uiState.loyaltyStamps,
                    maxStamps = uiState.maxLoyaltyStamps,
                    canRedeem = uiState.canRedeemStamps,
                    isRedeeming = uiState.isRedeeming,
                    onRedeemClick = { viewModel.onEvent(RewardsUiEvent.RedeemStamps) },
                    onCardClick = {
                        viewModel.onEvent(RewardsUiEvent.LoyaltyCardClicked)
                        onLoyaltyCardClick()
                    }
                )
            }

            // Available Rewards Section - User picks which reward to redeem
            item {
                Text(
                    text = "Đổi Điểm Thưởng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                Text(
                    text = "Chọn phần thưởng bạn muốn đổi",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Available Rewards Section
            if (uiState.availableRewards.isNotEmpty()) {
                items(uiState.availableRewards.filter { !it.isRedeemed }) { reward ->
                    RedeemableRewardItem(
                        reward = reward,
                        currentPoints = uiState.rewardPoints,
                        canRedeem = uiState.canRedeemReward(reward),
                        isRedeeming = uiState.isRedeeming,
                        onRedeemClick = {
                            viewModel.onEvent(RewardsUiEvent.SelectRewardToRedeem(reward))
                        }
                    )
                }
            } else {
                item {
                    EmptyRewardsPlaceholder()
                }
            }

            // History Rewards Section
            item {
                Text(
                    text = "History Rewards",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            if (uiState.historyItems.isEmpty()) {
                item {
                    EmptyHistoryPlaceholder()
                }
            } else {
                items(uiState.historyItems) { item ->
                    HistoryRewardItemNew(item = item)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RewardsTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Rewards",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * Large circular progress card showing current points
 */
@Composable
fun PointsProgressCard(
    currentPoints: Int,
    maxPoints: Int,
    progress: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Points",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Circular Progress Indicator with Points
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.White.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Progress arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = AppColors.Secondary,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Center content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentPoints.toString(),
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pts",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress text
            Text(
                text = "$currentPoints / $maxPoints points",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Points until next reward
            val pointsUntilFreeCoffee = (RewardsUiState.TIER_2_POINTS - currentPoints).coerceAtLeast(0)
            if (pointsUntilFreeCoffee > 0) {
                Text(
                    text = "Còn $pointsUntilFreeCoffee điểm để đổi thưởng!",
                    color = AppColors.Secondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Có thể đổi thưởng!",
                        color = AppColors.Secondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Redeem Free Coffee Card
 */
@Composable
fun RedeemFreeCoffeeCard(
    currentPoints: Int,
    requiredPoints: Int,
    canRedeem: Boolean,
    isRedeeming: Boolean,
    onRedeemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canRedeem) AppColors.Secondary else AppColors.LightBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Coffee icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (canRedeem) Color.White.copy(alpha = 0.2f) else AppColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = null,
                        tint = if (canRedeem) Color.White else AppColors.Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = "Free Coffee",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canRedeem) Color.White else AppColors.Primary
                    )
                    Text(
                        text = "$requiredPoints pts required",
                        fontSize = 14.sp,
                        color = if (canRedeem) Color.White.copy(alpha = 0.8f) else AppColors.GrayText
                    )
                }
            }

            Button(
                onClick = onRedeemClick,
                enabled = canRedeem && !isRedeeming,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canRedeem) Color.White else AppColors.Primary.copy(alpha = 0.3f),
                    contentColor = if (canRedeem) AppColors.Secondary else Color.White,
                    disabledContainerColor = Color.White.copy(alpha = 0.5f),
                    disabledContentColor = AppColors.GrayText
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                if (isRedeeming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AppColors.Secondary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (canRedeem) "Redeem" else "Need ${requiredPoints - currentPoints} more",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * Available reward item
 */
@Composable
fun AvailableRewardItem(
    coffeeName: String,
    pointsRequired: Int,
    validUntil: String,
    canRedeem: Boolean,
    onRedeemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canRedeem) { onRedeemClick() },
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
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.Primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = coffeeName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Primary
                    )
                    Text(
                        text = validUntil,
                        fontSize = 12.sp,
                        color = AppColors.GrayText
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$pointsRequired pts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canRedeem) AppColors.Secondary else AppColors.GrayText
                )
                if (canRedeem) {
                    Text(
                        text = "Available",
                        fontSize = 11.sp,
                        color = AppColors.Success
                    )
                }
            }
        }
    }
}

/**
 * History reward item with new design
 */
@Composable
fun HistoryRewardItemNew(item: RewardHistory) {
    val isEarned = item.type == RewardType.EARNED || item.points > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon based on type
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isEarned) AppColors.Success.copy(alpha = 0.1f)
                        else AppColors.Secondary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEarned) Icons.Default.Add else Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = if (isEarned) AppColors.Success else AppColors.Secondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = item.coffeeName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary
                )
                Text(
                    text = item.date,
                    fontSize = 12.sp,
                    color = AppColors.GrayText
                )
            }
        }

        Text(
            text = if (isEarned && item.points > 0) "+ ${item.points} Pts" else "${item.points} Pts",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isEarned && item.points > 0) AppColors.Success else AppColors.Secondary
        )
    }
}

/**
 * Empty history placeholder
 */
@Composable
fun EmptyHistoryPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
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
            text = "No reward history yet",
            fontSize = 14.sp,
            color = AppColors.GrayText,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Start earning points by placing orders!",
            fontSize = 12.sp,
            color = AppColors.GrayText.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

// Legacy components for compatibility
@Composable
fun LoyaltyCard(
    currentPoints: Int,
    maxPoints: Int,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LoyaltyCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Loyalty card",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "$currentPoints / $maxPoints",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
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
                        .padding(horizontal = 8.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(maxPoints) { index ->
                        CoffeeStampIcon(isFilled = index < currentPoints)
                    }
                }
            }
        }
    }
}

@Composable
fun CoffeeStampIcon(isFilled: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isFilled) AppColors.LoyaltyCard else AppColors.LightBackground
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                    .background(if (isFilled) Color.White else AppColors.GrayText.copy(alpha = 0.5f))
            )
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(3.dp)
                    .background(if (isFilled) Color.White else AppColors.GrayText.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
fun MyPointsCard(
    points: Int,
    onRedeemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Points:",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = points.toString(),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onRedeemClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Redeem drinks",
                    color = AppColors.Primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HistoryRewardItem(item: RewardHistoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.date,
                fontSize = 12.sp,
                color = AppColors.GrayText
            )
        }

        Text(
            text = "+ ${item.points} Pts",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Primary
        )
    }
}

@Composable
fun RewardsBottomNav(
    selectedIndex: Int = 1,
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
            RewardsBottomNavItem(
                icon = Icons.Outlined.Home,
                isSelected = selectedIndex == 0,
                onClick = onHomeClick
            )
            RewardsBottomNavItem(
                icon = Icons.Outlined.CardGiftcard,
                isSelected = selectedIndex == 1,
                onClick = onRewardsClick
            )
            RewardsBottomNavItem(
                icon = Icons.Outlined.Receipt,
                isSelected = selectedIndex == 2,
                onClick = onOrdersClick
            )
        }
    }
}

@Composable
fun RewardsBottomNavItem(
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

val sampleHistory = listOf(
    RewardHistoryItem(1, "Americano", 12, "24 June | 12:30 PM"),
    RewardHistoryItem(2, "Cafe Latte", 12, "22 June | 08:30 AM"),
    RewardHistoryItem(3, "Green Tea Latte", 12, "16 June | 10:48 AM"),
    RewardHistoryItem(4, "Flat White", 12, "12 May | 11:25 AM")
)

/**
 * Confirmation dialog for redeeming a reward
 */
@Composable
fun RedeemConfirmationDialog(
    reward: com.example.thecodecup.domain.model.Reward,
    currentPoints: Int,
    isRedeeming: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isRedeeming) onDismiss() },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        icon = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "Xác nhận đổi thưởng",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = AppColors.Primary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = reward.coffeeName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.LightBackground)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AppColors.Secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${reward.pointsRequired} điểm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AppColors.Secondary
                        )
                    }
                }

                Text(
                    text = "Số điểm còn lại sau khi đổi: ${currentPoints - reward.pointsRequired} điểm",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isRedeeming,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                if (isRedeeming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Xác nhận", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isRedeeming
            ) {
                Text("Hủy", color = AppColors.TextSecondary)
            }
        }
    )
}

/**
 * Loyalty card with redeem button when full
 */
@Composable
fun LoyaltyCardWithRedeem(
    currentStamps: Int,
    maxStamps: Int,
    canRedeem: Boolean,
    isRedeeming: Boolean,
    onRedeemClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        com.example.thecodecup.presentation.components.cards.SharedLoyaltyCard(
            currentStamps = currentStamps,
            maxStamps = maxStamps,
            onClick = onCardClick
        )

        if (canRedeem) {
            Button(
                onClick = onRedeemClick,
                enabled = !isRedeeming,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
            ) {
                if (isRedeeming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Redeem,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đổi ngay - Nhận 1 ly miễn phí!",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Redeemable reward item with redeem button
 */
@Composable
fun RedeemableRewardItem(
    reward: com.example.thecodecup.domain.model.Reward,
    currentPoints: Int,
    canRedeem: Boolean,
    isRedeeming: Boolean,
    onRedeemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canRedeem) AppColors.LightBackground else Color.White
        ),
        border = if (!canRedeem) androidx.compose.foundation.BorderStroke(
            1.dp,
            AppColors.Divider
        ) else null
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (canRedeem) AppColors.Primary.copy(alpha = 0.15f)
                            else AppColors.GrayText.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = null,
                        tint = if (canRedeem) AppColors.Primary else AppColors.GrayText,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column {
                    Text(
                        text = reward.coffeeName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (canRedeem) AppColors.TextPrimary else AppColors.GrayText
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (canRedeem) AppColors.Secondary else AppColors.GrayText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${reward.pointsRequired} điểm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (canRedeem) AppColors.Secondary else AppColors.GrayText
                        )
                    }
                    if (!canRedeem) {
                        Text(
                            text = "Cần thêm ${reward.pointsRequired - currentPoints} điểm",
                            fontSize = 12.sp,
                            color = AppColors.Error
                        )
                    }
                }
            }

            // Redeem Button
            Button(
                onClick = onRedeemClick,
                enabled = canRedeem && !isRedeeming,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    disabledContainerColor = AppColors.GrayText.copy(alpha = 0.3f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Đổi",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Empty rewards placeholder
 */
@Composable
fun EmptyRewardsPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = AppColors.GrayText,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Chưa có phần thưởng",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.GrayText
            )
            Text(
                text = "Tiếp tục mua sắm để tích điểm và đổi thưởng nhé!",
                fontSize = 14.sp,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RewardsScreenPreview() {
    RewardsScreen()
}
