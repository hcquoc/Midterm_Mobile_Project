package com.example.thecodecup.presentation.cart

// Cart Screen - Uses domain.model.CartItem for cart items display
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.presentation.components.colors.AppColors
import com.example.thecodecup.presentation.utils.PriceFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit = {},
    onCheckout: (orderId: String) -> Unit = {},
    viewModel: CartViewModel = viewModel(factory = CartViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle order success - navigate to success screen with orderId
    LaunchedEffect(uiState.orderSuccess, uiState.orderId) {
        if (uiState.orderSuccess && uiState.orderId != null) {
            onCheckout(uiState.orderId!!)
            viewModel.onEvent(CartUiEvent.ConsumeOrderSuccess)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { CartTopBar(onBackClick = onBackClick) },
        bottomBar = {
            if (!uiState.isEmpty) {
                CartBottomBar(
                    subtotal = uiState.subtotal,
                    deliveryFee = uiState.deliveryFee,
                    totalPrice = uiState.totalPrice,
                    isLoading = uiState.isCheckingOut,
                    onCheckout = {
                        viewModel.onEvent(CartUiEvent.PlaceOrder())
                    }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isEmpty) {
            // Empty Cart State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = AppColors.GrayText,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty",
                        fontSize = 18.sp,
                        color = AppColors.GrayText,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add some coffee to get started!",
                        fontSize = 14.sp,
                        color = AppColors.GrayText
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    SwipeToDeleteCartItem(
                        item = item,
                        onDelete = {
                            viewModel.onEvent(CartUiEvent.RemoveItem(item.id))
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CartTopBar(onBackClick: () -> Unit = {}) {
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
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "My Cart",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.Primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteCartItem(
    item: CartItem,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Delete background when swiping
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> AppColors.Error.copy(alpha = 0.1f)
                    else -> Color.Transparent
                },
                label = "swipe_color"
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0.75f,
                label = "swipe_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AppColors.Error,
                    modifier = Modifier.scale(scale)
                )
            }
        },
        content = {
            CartItemCard(item = item)
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    )
}

@Composable
fun CartItemCard(item: CartItem) {
    val context = LocalContext.current
    // Get image resource ID - return 0 if not found (will use fallback)
    val imageResId = remember(item.coffee.imageName) {
        if (item.coffee.imageName.isNullOrBlank()) {
            0
        } else {
            try {
                context.resources.getIdentifier(
                    item.coffee.imageName,
                    "drawable",
                    context.packageName
                )
            } catch (_: Exception) {
                0
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coffee Image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (imageResId > 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = item.coffee.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = item.coffee.name,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Item Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.coffee.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.getDetailsString(),
                    fontSize = 12.sp,
                    color = AppColors.GrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "x${item.quantity}",
                    fontSize = 12.sp,
                    color = AppColors.GrayText,
                    fontWeight = FontWeight.Medium
                )
            }

            // Price
            Text(
                text = PriceFormatter.formatVND(item.totalPrice),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary
            )
        }
    }
}

@Composable
fun CartBottomBar(
    subtotal: Double,
    deliveryFee: Double,
    totalPrice: Double,
    isLoading: Boolean = false,
    onCheckout: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Price breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tạm tính",
                    fontSize = 11.sp,
                    color = AppColors.GrayText
                )
                Text(
                    text = PriceFormatter.formatVND(subtotal),
                    fontSize = 11.sp,
                    color = AppColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Phí giao hàng",
                    fontSize = 11.sp,
                    color = AppColors.GrayText
                )
                Text(
                    text = PriceFormatter.formatVND(deliveryFee),
                    fontSize = 11.sp,
                    color = AppColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total Price
                Column {
                    Text(
                        text = "Tổng cộng",
                        fontSize = 11.sp,
                        color = AppColors.GrayText
                    )
                    Text(
                        text = PriceFormatter.formatVND(totalPrice),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }

            // Checkout Button
            Button(
                onClick = onCheckout,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Secondary,
                    disabledContainerColor = AppColors.Disabled
                ),
                contentPadding = PaddingValues(horizontal = 20.dp),
                enabled = !isLoading,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isLoading) "Processing..." else "Checkout",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    CartScreen()
}

