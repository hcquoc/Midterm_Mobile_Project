package com.example.thecodecup.presentation.order

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecodecup.presentation.components.colors.AppColors

@Composable
fun OrderTrackingScreen(
    orderId: String,
    onBackClick: () -> Unit = {},
    context: Context = LocalContext.current,
    viewModel: OrderTrackingViewModel = viewModel {
        OrderTrackingViewModel(context = context)
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentStep by viewModel.fakeStatusStep.collectAsState()

    // Load order when screen opens
    LaunchedEffect(orderId) {
        viewModel.onEvent(OrderTrackingUiEvent.LoadOrder(orderId))
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            OrderTrackingTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Order ID
            if (uiState.order != null) {
                Text(
                    text = "Đơn hàng #${uiState.order?.id?.takeLast(8)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.GrayText
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Status Icon with Animation
            TrackingStatusIcon(
                currentStep = currentStep,
                isCompleted = uiState.isCompleted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status Title
            Text(
                text = when (currentStep) {
                    0 -> "Đơn hàng đã được xác nhận"
                    1 -> "Đang pha chế đồ uống"
                    2 -> "Shipper đang giao hàng"
                    3 -> "Giao hàng thành công!"
                    else -> "Đang xử lý..."
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Estimated time
            if (!uiState.isCompleted) {
                Text(
                    text = when (currentStep) {
                        0 -> "Đang xử lý đơn hàng của bạn..."
                        1 -> "Còn khoảng 5-10 phút"
                        2 -> "Shipper sẽ đến trong ít phút"
                        else -> ""
                    },
                    fontSize = 14.sp,
                    color = AppColors.GrayText,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tracking Steps List
            TrackingStepsList(
                steps = uiState.trackingSteps,
                currentStep = currentStep
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Simulation Status
            if (uiState.isSimulating) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AppColors.Secondary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đang cập nhật trạng thái...",
                        fontSize = 12.sp,
                        color = AppColors.GrayText
                    )
                }
            }

            // Completed Message
            if (uiState.isCompleted) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.Success.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AppColors.Success,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Cảm ơn bạn!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.Success
                            )
                            Text(
                                text = "Chúc bạn ngon miệng ❤️",
                                fontSize = 14.sp,
                                color = AppColors.GrayText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTrackingTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AppColors.Primary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Theo dõi đơn hàng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Primary
        )
    }
}

@Composable
fun TrackingStatusIcon(
    currentStep: Int,
    isCompleted: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.1f else 1f,
        animationSpec = tween(500),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCompleted -> AppColors.Success
            currentStep == 2 -> AppColors.Secondary
            currentStep == 1 -> AppColors.Warning
            else -> AppColors.Primary
        },
        animationSpec = tween(500),
        label = "bgColor"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(backgroundColor.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (currentStep) {
                        0 -> Icons.Default.Receipt
                        1 -> Icons.Default.LocalCafe
                        2 -> Icons.Default.DeliveryDining
                        3 -> Icons.Default.CheckCircle
                        else -> Icons.Default.HourglassEmpty
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun TrackingStepsList(
    steps: List<TrackingStep>,
    currentStep: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        steps.forEachIndexed { index, step ->
            TrackingStepItem(
                step = step,
                isLast = index == steps.lastIndex,
                currentStep = currentStep
            )
        }
    }
}

@Composable
fun TrackingStepItem(
    step: TrackingStep,
    isLast: Boolean,
    currentStep: Int
) {
    val isActive = step.step <= currentStep

    val dotColor by animateColorAsState(
        targetValue = when {
            step.isCompleted -> AppColors.Success
            step.isCurrent -> AppColors.Secondary
            else -> AppColors.GrayText.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "dotColor"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            step.isCompleted || step.isCurrent -> AppColors.Primary
            else -> AppColors.GrayText
        },
        animationSpec = tween(300),
        label = "textColor"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dot
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(dotColor),
                contentAlignment = Alignment.Center
            ) {
                if (step.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else if (step.isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // Line (if not last)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(50.dp)
                        .background(
                            if (step.isCompleted) AppColors.Success.copy(alpha = 0.5f)
                            else AppColors.GrayText.copy(alpha = 0.2f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!isLast) 26.dp else 0.dp)
        ) {
            Text(
                text = step.title,
                fontSize = 16.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.description,
                fontSize = 13.sp,
                color = AppColors.GrayText
            )
        }

        // Status indicator
        if (step.isCurrent && !step.isCompleted) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = AppColors.Secondary,
                strokeWidth = 2.dp
            )
        } else if (step.isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AppColors.Success,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderTrackingScreenPreview() {
    // Preview with mock data
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        TrackingStepsList(
            steps = listOf(
                TrackingStep(0, "Đã đặt", "Đơn hàng đã được xác nhận", isCompleted = true),
                TrackingStep(1, "Đang pha chế", "Barista đang chuẩn bị đồ uống", isCurrent = true),
                TrackingStep(2, "Đang giao hàng", "Shipper đang trên đường giao"),
                TrackingStep(3, "Hoàn tất", "Đơn hàng đã được giao thành công")
            ),
            currentStep = 1
        )
    }
}

