package com.example.realtimepricetrackerapp.data.model

data class PriceUpdate(
    val symbol: String,
    val price: Double,
    val timestamp: Long,
    val priceChange: PriceChange
)

enum class PriceChange {
    UP,
    DOWN,
    NEUTRAL
}
