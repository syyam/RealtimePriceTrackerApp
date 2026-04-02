package com.example.realtimepricetrackerapp.data.websocket

import com.example.realtimepricetrackerapp.data.model.ConnectionState
import com.example.realtimepricetrackerapp.data.model.PriceChange
import com.example.realtimepricetrackerapp.data.model.PriceUpdate
import com.example.realtimepricetrackerapp.data.model.StockSymbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import kotlin.random.Random

object WebSocketManager {
    private const val WS_URL = "wss://ws.postman-echo.com/raw"
    private const val PRICE_UPDATE_INTERVAL = 2000L // 2 seconds

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var updateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(replay = 0)
    val priceUpdates: SharedFlow<PriceUpdate> = _priceUpdates.asSharedFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val previousPrices = mutableMapOf<String, Double>()
    private var activeSymbols = listOf<StockSymbol>()

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.value = ConnectionState.Connected
            startPriceUpdates()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val json = JSONObject(text)
                val symbol = json.getString("symbol")
                val price = json.getDouble("price")
                val timestamp = System.currentTimeMillis()

                val priceChange = when {
                    !previousPrices.containsKey(symbol) -> PriceChange.NEUTRAL
                    price > previousPrices[symbol]!! -> PriceChange.UP
                    price < previousPrices[symbol]!! -> PriceChange.DOWN
                    else -> PriceChange.NEUTRAL
                }

                previousPrices[symbol] = price

                scope.launch {
                    _priceUpdates.emit(
                        PriceUpdate(
                            symbol = symbol,
                            price = price,
                            timestamp = timestamp,
                            priceChange = priceChange
                        )
                    )
                }
            } catch (e: Exception) {
                // ignore parsing errors
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
            _connectionState.value = ConnectionState.Disconnected
            updateJob?.cancel()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _connectionState.value = ConnectionState.Error(t.message ?: "Connection failed")
            updateJob?.cancel()
        }
    }

    fun connect(symbols: List<StockSymbol>) {
        if (_connectionState.value is ConnectionState.Connected ||
            _connectionState.value is ConnectionState.Connecting) {
            return
        }

        activeSymbols = symbols
        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder()
            .url(WS_URL)
            .build()

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    private fun startPriceUpdates() {
        updateJob?.cancel()
        updateJob = scope.launch {
            while (true) {
                delay(PRICE_UPDATE_INTERVAL)

                activeSymbols.forEach { stockSymbol ->
                    val price = Random.nextDouble(50.0, 500.0)
                    val json = JSONObject().apply {
                        put("symbol", stockSymbol.symbol)
                        put("price", price)
                    }

                    webSocket?.send(json.toString())
                }
            }
        }
    }

    fun disconnect() {
        updateJob?.cancel()
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        previousPrices.clear()
        activeSymbols = emptyList()
        _connectionState.value = ConnectionState.Disconnected
    }

    fun isConnected(): Boolean {
        return _connectionState.value is ConnectionState.Connected
    }
}
