package com.example.realtimepricetrackerapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.realtimepricetrackerapp.data.model.PriceChange

@Composable
fun PriceChangeIndicator(priceChange: PriceChange) {
    val (symbol, color) = when (priceChange) {
        PriceChange.UP -> "↑" to Color(0xFF4CAF50)
        PriceChange.DOWN -> "↓" to Color(0xFFF44336)
        PriceChange.NEUTRAL -> "—" to Color.Gray
    }

    Text(
        text = symbol,
        color = color,
        style = MaterialTheme.typography.titleMedium
    )
}
