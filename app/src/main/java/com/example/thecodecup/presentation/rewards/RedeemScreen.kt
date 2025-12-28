package com.example.thecodecup.presentation.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.presentation.components.colors.AppColors

data class RedeemItem(
    val id: Int,
    val name: String,
    val validUntil: String,
    val points: Int,
    var isRedeemed: Boolean = false
)

@Composable
fun RedeemScreen(
    currentPoints: Int = 2750,
    onBackClick: () -> Unit = {},
    redeemItems: List<RedeemItem> = sampleRedeemItems,
    onRedeemItem: (RedeemItem) -> Unit = {}
) {
    var points by remember { mutableIntStateOf(currentPoints) }
    var items by remember { mutableStateOf(redeemItems) }

    Scaffold(
        containerColor = Color.White,
        topBar = { RedeemTopBar(onBackClick = onBackClick) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(items) { item ->
                RedeemItemCard(
                    item = item,
                    canRedeem = points >= item.points && !item.isRedeemed,
                    onRedeem = {
                        if (points >= item.points && !item.isRedeemed) {
                            points -= item.points
                            items = items.map {
                                if (it.id == item.id) it.copy(isRedeemed = true) else it
                            }
                            onRedeemItem(item)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RedeemTopBar(onBackClick: () -> Unit) {
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
            text = "Redeem",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.Primary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Invisible spacer for centering
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
fun RedeemItemCard(
    item: RedeemItem,
    canRedeem: Boolean,
    onRedeem: () -> Unit
) {
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
            // Coffee Image placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalCafe,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.validUntil,
                    fontSize = 12.sp,
                    color = AppColors.GrayText
                )
            }

            // Points Badge / Redeem Button
            Button(
                onClick = onRedeem,
                enabled = canRedeem,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.isRedeemed) AppColors.Success else AppColors.Secondary,
                    disabledContainerColor = AppColors.GrayText.copy(alpha = 0.3f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (item.isRedeemed) "Redeemed" else "${item.points} pts",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

val sampleRedeemItems = listOf(
    RedeemItem(1, "Cafe Latte", "Valid until 04.07.21", 1340),
    RedeemItem(2, "Flat White", "Valid until 04.07.21", 1340),
    RedeemItem(3, "Cappuccino", "Valid until 04.07.21", 1340),
    RedeemItem(4, "Americano", "Valid until 04.07.21", 1340)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RedeemScreenPreview() {
    RedeemScreen()
}

