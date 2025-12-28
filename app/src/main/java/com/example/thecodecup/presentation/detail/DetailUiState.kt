package com.example.thecodecup.presentation.detail

import com.example.thecodecup.domain.model.Coffee
import com.example.thecodecup.domain.model.CoffeeOptions
import com.example.thecodecup.domain.model.Ice
import com.example.thecodecup.domain.model.Shot
import com.example.thecodecup.domain.model.Size
import com.example.thecodecup.domain.model.Temperature

/**
 * UI State for Detail Screen
 */
data class DetailUiState(
    val coffee: Coffee? = null,
    val quantity: Int = 1,
    val shot: Shot = Shot.SINGLE,
    val temperature: Temperature = Temperature.ICED,
    val size: Size = Size.MEDIUM,
    val ice: Ice = Ice.FULL,
    val isLoading: Boolean = false,
    val isAddingToCart: Boolean = false,
    val addedToCart: Boolean = false,
    val errorMessage: String? = null
) {
    val options: CoffeeOptions
        get() = CoffeeOptions(shot, temperature, size, ice)

    val unitPrice: Double
        get() = (coffee?.basePrice ?: 0.0) + options.calculateExtraPrice()

    val totalPrice: Double
        get() = unitPrice * quantity
}

/**
 * Events from Detail Screen
 */
sealed interface DetailUiEvent {
    data class SetQuantity(val quantity: Int) : DetailUiEvent
    data class SetShot(val shot: Shot) : DetailUiEvent
    data class SetTemperature(val temperature: Temperature) : DetailUiEvent
    data class SetSize(val size: Size) : DetailUiEvent
    data class SetIce(val ice: Ice) : DetailUiEvent
    data object AddToCart : DetailUiEvent
    data object ConsumeAddedToCart : DetailUiEvent
    data object ClearError : DetailUiEvent
    data object NavigateBack : DetailUiEvent
    data object NavigateToCart : DetailUiEvent
}
