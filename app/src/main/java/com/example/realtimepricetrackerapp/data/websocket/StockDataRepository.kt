package com.example.realtimepricetrackerapp.data.websocket

import com.example.realtimepricetrackerapp.data.model.ConnectionState
import com.example.realtimepricetrackerapp.data.model.PriceUpdate
import com.example.realtimepricetrackerapp.data.model.StockSymbol
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class StockDataRepository {
    val priceUpdates: SharedFlow<PriceUpdate> = WebSocketManager.priceUpdates
    val connectionState: StateFlow<ConnectionState> = WebSocketManager.connectionState

    fun connect(symbols: List<StockSymbol>) {
        WebSocketManager.connect(symbols)
    }

    fun disconnect() {
        WebSocketManager.disconnect()
    }

    fun isConnected(): Boolean {
        return WebSocketManager.isConnected()
    }
}
