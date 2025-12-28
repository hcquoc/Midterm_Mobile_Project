package com.example.thecodecup.presentation.order

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thecodecup.R
import com.example.thecodecup.di.ServiceLocator
import com.example.thecodecup.domain.model.Order
import com.example.thecodecup.domain.model.OrderStatus
import com.example.thecodecup.domain.repository.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Tracking step data class
 */
data class TrackingStep(
    val step: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false
)

/**
 * UI State for Order Tracking Screen
 */
data class OrderTrackingUiState(
    val order: Order? = null,
    val currentStep: Int = 0,
    val isLoading: Boolean = false,
    val isSimulating: Boolean = false,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null
) {
    val trackingSteps: List<TrackingStep>
        get() = listOf(
            TrackingStep(
                step = 0,
                title = "Đã đặt",
                description = "Đơn hàng đã được xác nhận",
                isCompleted = currentStep > 0,
                isCurrent = currentStep == 0
            ),
            TrackingStep(
                step = 1,
                title = "Đang pha chế",
                description = "Barista đang chuẩn bị đồ uống",
                isCompleted = currentStep > 1,
                isCurrent = currentStep == 1
            ),
            TrackingStep(
                step = 2,
                title = "Đang giao hàng",
                description = "Shipper đang trên đường giao",
                isCompleted = currentStep > 2,
                isCurrent = currentStep == 2
            ),
            TrackingStep(
                step = 3,
                title = "Hoàn tất",
                description = "Đơn hàng đã được giao thành công",
                isCompleted = currentStep >= 3,
                isCurrent = currentStep == 3
            )
        )
}

/**
 * Events from Order Tracking Screen
 */
sealed interface OrderTrackingUiEvent {
    data class LoadOrder(val orderId: String) : OrderTrackingUiEvent
    data object StartSimulation : OrderTrackingUiEvent
    data object StopSimulation : OrderTrackingUiEvent
    data object ClearError : OrderTrackingUiEvent
}

/**
 * ViewModel for Order Tracking with Simulated Progress and Notifications
 */
class OrderTrackingViewModel(
    private val orderRepository: OrderRepository = ServiceLocator.provideOrderRepository(),
    private val context: Context? = null
) : ViewModel() {

    companion object {
        private const val CHANNEL_ID = "order_tracking_channel"
        private const val CHANNEL_NAME = "Order Tracking"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

    // Fake status step for simulation (0 to 3)
    private val _fakeStatusStep = MutableStateFlow(0)
    val fakeStatusStep: StateFlow<Int> = _fakeStatusStep.asStateFlow()

    private var simulationJob: Job? = null

    init {
        // Create notification channel
        createNotificationChannel()
    }

    fun onEvent(event: OrderTrackingUiEvent) {
        when (event) {
            is OrderTrackingUiEvent.LoadOrder -> loadOrder(event.orderId)
            is OrderTrackingUiEvent.StartSimulation -> startSimulation()
            is OrderTrackingUiEvent.StopSimulation -> stopSimulation()
            is OrderTrackingUiEvent.ClearError -> clearError()
        }
    }

    private fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val order = orderRepository.getOrderById(orderId)
                _uiState.update { state ->
                    state.copy(
                        order = order,
                        isLoading = false,
                        currentStep = 0
                    )
                }

                // Auto-start simulation when order is loaded
                if (order != null && order.status == OrderStatus.ONGOING) {
                    startSimulation()
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /**
     * Start the order tracking simulation
     * Progresses through 4 steps with delays and notifications
     */
    private fun startSimulation() {
        // Cancel any existing simulation
        simulationJob?.cancel()

        simulationJob = viewModelScope.launch {
            _uiState.update { it.copy(isSimulating = true, isCompleted = false) }

            // Step 0: Đã đặt
            _fakeStatusStep.value = 0
            _uiState.update { it.copy(currentStep = 0) }
            delay(3000) // 3 seconds

            // Step 1: Đang pha chế
            _fakeStatusStep.value = 1
            _uiState.update { it.copy(currentStep = 1) }
            showNotification(
                title = "Đang pha chế ☕",
                message = "Barista đang pha cafe cho bạn"
            )
            delay(4000) // 4 seconds

            // Step 2: Đang giao hàng
            _fakeStatusStep.value = 2
            _uiState.update { it.copy(currentStep = 2) }
            showNotification(
                title = "Đang giao hàng 🛵",
                message = "Shipper đã nhận đơn và đang trên đường giao!"
            )
            delay(4000) // 4 seconds

            // Step 3: Hoàn tất
            _fakeStatusStep.value = 3
            _uiState.update { it.copy(currentStep = 3, isCompleted = true) }
            showNotification(
                title = "Giao hàng thành công! ❤️",
                message = "Cảm ơn bạn đã đặt hàng. Chúc bạn ngon miệng!"
            )

            // Update real database status to COMPLETED
            _uiState.value.order?.let { order ->
                try {
                    orderRepository.updateOrderStatus(order.id, OrderStatus.COMPLETED)
                } catch (e: Exception) {
                    // Log error but don't fail the simulation
                    android.util.Log.e("OrderTrackingVM", "Error updating order status", e)
                }
            }

            _uiState.update { it.copy(isSimulating = false) }
        }
    }

    private fun stopSimulation() {
        simulationJob?.cancel()
        _uiState.update { it.copy(isSimulating = false) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.let { ctx ->
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for order tracking updates"
                }

                val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun showNotification(title: String, message: String) {
        context?.let { ctx ->
            try {
                // Check notification permission for Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ctx.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        android.util.Log.w("OrderTrackingVM", "Notification permission not granted")
                        return@let
                    }
                }

                val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(
                    NOTIFICATION_ID_BASE + _fakeStatusStep.value,
                    notification
                )
            } catch (e: Exception) {
                android.util.Log.e("OrderTrackingVM", "Error showing notification", e)
            }
        }
    }

    private fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}

