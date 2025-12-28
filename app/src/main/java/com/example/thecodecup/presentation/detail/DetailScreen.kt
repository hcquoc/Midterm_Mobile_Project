package com.example.thecodecup.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thecodecup.domain.model.CartItem
import com.example.thecodecup.domain.model.Ice
import com.example.thecodecup.domain.model.Shot
import com.example.thecodecup.domain.model.Size
import com.example.thecodecup.domain.model.Temperature
import com.example.thecodecup.presentation.components.colors.AppColors
import com.example.thecodecup.presentation.utils.PriceFormatter


@Composable
fun DetailScreen(
    coffeeId: Int = 0,
    coffeeName: String = "Americano",
    onBackClick: () -> Unit = {},
    onNavigateToCart: () -> Unit = {}
) {
    android.util.Log.d("DetailScreen", "DetailScreen composing with coffeeId=$coffeeId, coffeeName=$coffeeName")

    // Create ViewModel with factory
    val viewModel: DetailViewModel = viewModel(factory = DetailViewModelFactory())

    android.util.Log.d("DetailScreen", "ViewModel created successfully")

    // Load coffee when screen opens
    LaunchedEffect(coffeeId) {
        android.util.Log.d("DetailScreen", "LaunchedEffect - loading coffee")
        viewModel.loadCoffee(coffeeId)
    }

    val uiState by viewModel.uiState.collectAsState()

    // Navigate to cart when item is added successfully
    LaunchedEffect(uiState.addedToCart) {
        if (uiState.addedToCart) {
            viewModel.onEvent(DetailUiEvent.ConsumeAddedToCart)
            onNavigateToCart()
        }
    }

    // --- STATE MANAGEMENT ---
    var quantity by remember { mutableIntStateOf(1) }
    var selectedShot by remember { mutableStateOf(Shot.SINGLE) }
    var selectedTemp by remember { mutableStateOf(Temperature.ICED) }
    var selectedSize by remember { mutableStateOf(Size.MEDIUM) }
    var selectedIce by remember { mutableStateOf(Ice.FULL) }

    // Success message state
    var showAddedMessage by remember { mutableStateOf(false) }

    // Dynamic price calculation using domain model
    val basePrice = uiState.coffee?.basePrice ?: 35000.0
    val options = com.example.thecodecup.domain.model.CoffeeOptions(selectedShot, selectedTemp, selectedSize, selectedIce)
    val totalPrice = (basePrice + options.calculateExtraPrice()) * quantity

    // Screen configuration
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val heroHeight = screenHeight * 0.38f

    val context = LocalContext.current
    // Get image resource ID
    val imageResId = remember(uiState.coffee?.imageName) {
        if (uiState.coffee?.imageName.isNullOrBlank()) {
            0
        } else {
            try {
                context.resources.getIdentifier(
                    uiState.coffee?.imageName,
                    "drawable",
                    context.packageName
                )
            } catch (e: Exception) {
                0
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
            ) {
                // Coffee Image
                if (imageResId > 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = coffeeName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback gradient background with icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        AppColors.Primary,
                                        AppColors.Primary.copy(alpha = 0.8f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalCafe,
                            contentDescription = coffeeName,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }

                // Gradient overlay for better readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Back Button - Semi-transparent circle
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 48.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Cart Button - Top Right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 48.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { onNavigateToCart() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Content Sheet - Overlapping the hero image
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = AppColors.Background,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 28.dp, bottom = 120.dp) // Bottom padding for sticky bar
                ) {
                    // Title Row with Favorite
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = coffeeName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary
                        )

                        // Favorite Button
                        IconButton(
                            onClick = { /* TODO: Add to favorites */ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AppColors.LightBackground)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Add to Favorites",
                                tint = AppColors.Primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AppColors.Star,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "4.8",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = AppColors.Primary
                        )
                        Text(
                            text = "(120 reviews)",
                            fontSize = 14.sp,
                            color = AppColors.GrayText
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = uiState.coffee?.description
                            ?: "A smooth and rich espresso-based beverage, crafted with premium beans and served just the way you like it. Perfect for any time of day.",
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = AppColors.GrayText
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Price Display
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Giá",
                            fontSize = 16.sp,
                            color = AppColors.GrayText
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = PriceFormatter.formatVND(basePrice),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Customization Section Header
                    Text(
                        text = "Customize Your Order",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options Cards
                    // Quantity
                    OptionCard(
                        title = "Quantity",
                        content = {
                            QuantitySelector(
                                quantity = quantity,
                                onIncrease = {
                                    quantity++
                                    showAddedMessage = false
                                },
                                onDecrease = {
                                    if (quantity > 1) {
                                        quantity--
                                        showAddedMessage = false
                                    }
                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shot Option
                    OptionCard(
                        title = "Shot",
                        content = {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                PillButton(
                                    text = "Single",
                                    isSelected = selectedShot == Shot.SINGLE,
                                    onClick = {
                                        selectedShot = Shot.SINGLE
                                        showAddedMessage = false
                                    }
                                )
                                PillButton(
                                    text = "Double",
                                    isSelected = selectedShot == Shot.DOUBLE,
                                    onClick = {
                                        selectedShot = Shot.DOUBLE
                                        showAddedMessage = false
                                    }
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Temperature Option
                    OptionCard(
                        title = "Temperature",
                        content = {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                SelectableChip(
                                    text = "Hot",
                                    icon = Icons.Default.LocalCafe,
                                    isSelected = selectedTemp == Temperature.HOT,
                                    onClick = {
                                        selectedTemp = Temperature.HOT
                                        showAddedMessage = false
                                    }
                                )
                                SelectableChip(
                                    text = "Iced",
                                    icon = Icons.Default.LocalDrink,
                                    isSelected = selectedTemp == Temperature.ICED,
                                    onClick = {
                                        selectedTemp = Temperature.ICED
                                        showAddedMessage = false
                                    }
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Size Option
                    OptionCard(
                        title = "Size",
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SizeChip(
                                    text = "S",
                                    subtext = "Small",
                                    isSelected = selectedSize == Size.SMALL,
                                    onClick = {
                                        selectedSize = Size.SMALL
                                        showAddedMessage = false
                                    }
                                )
                                SizeChip(
                                    text = "M",
                                    subtext = "Medium",
                                    isSelected = selectedSize == Size.MEDIUM,
                                    onClick = {
                                        selectedSize = Size.MEDIUM
                                        showAddedMessage = false
                                    }
                                )
                                SizeChip(
                                    text = "L",
                                    subtext = "Large",
                                    isSelected = selectedSize == Size.LARGE,
                                    onClick = {
                                        selectedSize = Size.LARGE
                                        showAddedMessage = false
                                    }
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ice Option
                    OptionCard(
                        title = "Ice Level",
                        content = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IceChip(
                                    text = "Less",
                                    level = 1,
                                    isSelected = selectedIce == Ice.LESS,
                                    onClick = {
                                        selectedIce = Ice.LESS
                                        showAddedMessage = false
                                    }
                                )
                                IceChip(
                                    text = "Normal",
                                    level = 2,
                                    isSelected = selectedIce == Ice.NORMAL,
                                    onClick = {
                                        selectedIce = Ice.NORMAL
                                        showAddedMessage = false
                                    }
                                )
                                IceChip(
                                    text = "Full",
                                    level = 3,
                                    isSelected = selectedIce == Ice.FULL,
                                    onClick = {
                                        selectedIce = Ice.FULL
                                        showAddedMessage = false
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }

        // Sticky Bottom Action Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = AppColors.Surface,
            shadowElevation = 20.dp,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                // Success message
                if (showAddedMessage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(AppColors.Success.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AppColors.Success,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Added to cart!",
                            color = AppColors.Success,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "View Cart",
                            color = AppColors.Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToCart() }
                        )
                    }
                }

                // Price and Add to Cart Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Total Price
                    Column {
                        Text(
                            text = "Tổng cộng",
                            fontSize = 14.sp,
                            color = AppColors.GrayText
                        )
                        Text(
                            text = PriceFormatter.formatVND(totalPrice),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary
                        )
                    }

                    // Add to Cart Button
                    Button(
                        onClick = {
                            viewModel.onEvent(DetailUiEvent.SetQuantity(quantity))
                            viewModel.onEvent(DetailUiEvent.SetShot(selectedShot))
                            viewModel.onEvent(DetailUiEvent.SetTemperature(selectedTemp))
                            viewModel.onEvent(DetailUiEvent.SetSize(selectedSize))
                            viewModel.onEvent(DetailUiEvent.SetIce(selectedIce))
                            viewModel.onEvent(DetailUiEvent.AddToCart)
                            showAddedMessage = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 24.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Secondary),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 10.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to Cart",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// --- UI COMPONENTS ---

@Composable
fun OptionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.SurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )
            content()
        }
    }
}

@Composable
fun SelectableChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppColors.Secondary else AppColors.CardBackground,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else AppColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else AppColors.TextSecondary
            )
        }
    }
}

@Composable
fun SizeChip(
    text: String,
    subtext: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppColors.Primary else Color.White,
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else AppColors.Primary
            )
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else AppColors.GrayText
            )
        }
    }
}

@Composable
fun IceChip(
    text: String,
    level: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AppColors.Primary else Color.White,
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ice cube icons
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(level) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else AppColors.GrayText,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else AppColors.GrayText
            )
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            // Decrease Button
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onDecrease() },
                shape = RoundedCornerShape(10.dp),
                color = AppColors.LightBackground
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = AppColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "$quantity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Increase Button
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onIncrease() },
                shape = RoundedCornerShape(10.dp),
                color = AppColors.Primary
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PillButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) AppColors.Primary else Color.White,
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else AppColors.GrayText,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

// Keep legacy components for compatibility
@Composable
fun DetailTopBar(
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    cartItemCount: Int = 0
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = AppColors.Primary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = "Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Primary,
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onCartClick() }
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = "Cart",
                tint = AppColors.Primary,
                modifier = Modifier.size(24.dp)
            )

            if (cartItemCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                        .size(16.dp)
                        .background(AppColors.Success, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cartItemCount > 9) "9+" else cartItemCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OptionRow(
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Primary
        )
        content()
    }
}

@Composable
fun IconOptionButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) AppColors.Primary else AppColors.GrayText.copy(alpha = 0.5f),
        modifier = Modifier
            .size(32.dp)
            .clickable { onClick() }
    )
}

@Composable
fun SizeIconButton(
    size: Dp,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.LocalDrink,
        contentDescription = null,
        tint = if (isSelected) AppColors.Primary else AppColors.GrayText.copy(alpha = 0.5f),
        modifier = Modifier
            .size(size)
            .clickable { onClick() }
    )
}

@Composable
fun IceIconButton(
    level: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor = if (isSelected) AppColors.Primary else AppColors.GrayText.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        when (level) {
            1 -> {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(1.5.dp, iconColor, RoundedCornerShape(3.dp))
                )
            }
            2 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .border(1.5.dp, iconColor, RoundedCornerShape(3.dp))
                    )
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .border(1.5.dp, iconColor, RoundedCornerShape(3.dp))
                    )
                }
            }
            3 -> {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.5.dp, iconColor, RoundedCornerShape(2.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.5.dp, iconColor, RoundedCornerShape(2.dp))
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.5.dp, iconColor, RoundedCornerShape(2.dp))
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.5.dp, iconColor, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddToCartBottomBar(
    totalPrice: Double,
    showAddedMessage: Boolean = false,
    onAddToCart: () -> Unit = {},
    onNavigateToCart: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(24.dp)
    ) {
        if (showAddedMessage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .background(AppColors.Success.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppColors.Success,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Added to cart!",
                    color = AppColors.Success,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "View Cart",
                    color = AppColors.Primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToCart() }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tổng tiền",
                fontSize = 16.sp,
                color = AppColors.GrayText
            )
            Text(
                text = PriceFormatter.formatVND(totalPrice),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddToCart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Secondary),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
            Text(
                text = "Add to cart",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Cart Preview Dialog
@Composable
fun CartPreviewDialog(
    cartItems: List<CartItem>,
    totalPrice: Double,
    onDismiss: () -> Unit,
    onGoToCart: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cart Preview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppColors.GrayText,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = AppColors.GrayText,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your cart is empty",
                                color = AppColors.GrayText
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems) { item ->
                            CartPreviewItem(item = item)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng:",
                            fontWeight = FontWeight.Medium,
                            color = AppColors.GrayText
                        )
                        Text(
                            text = PriceFormatter.formatVND(totalPrice),
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onGoToCart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(text = "Go to Cart", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun CartPreviewItem(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.LightBackground, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(AppColors.Primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalCafe,
                contentDescription = null,
                tint = AppColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.coffee.name,
                fontWeight = FontWeight.Medium,
                color = AppColors.Primary,
                fontSize = 14.sp
            )
            Text(
                text = item.getDetailsString(),
                color = AppColors.GrayText,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "x${item.quantity}",
                color = AppColors.GrayText,
                fontSize = 12.sp
            )
            Text(
                text = PriceFormatter.formatVND(item.totalPrice),
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Error screen shown when ViewModel creation fails
 */
@Composable
fun ErrorDetailScreen(
    coffeeName: String,
    errorMessage: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.Primary
                    )
                }
                Text(
                    text = coffeeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = AppColors.Error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Unable to load product",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = AppColors.GrayText,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Go Back")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen()
}
