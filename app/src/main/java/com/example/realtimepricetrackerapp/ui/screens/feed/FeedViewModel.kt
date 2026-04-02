package com.example.realtimepricetrackerapp.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realtimepricetrackerapp.data.model.ConnectionState
import com.example.realtimepricetrackerapp.data.model.PriceUpdate
import com.example.realtimepricetrackerapp.data.websocket.StockDataRepository
import com.example.realtimepricetrackerapp.util.StockSymbols
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val priceUpdates: Map<String, PriceUpdate> = emptyMap(),
    val sortedUpdates: List<PriceUpdate> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Disconnected
)

class FeedViewModel : ViewModel() {
    private val repository = StockDataRepository()

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.priceUpdates.collect { priceUpdate ->
                _uiState.update { currentState ->
                    val updatedMap = currentState.priceUpdates.toMutableMap().apply {
                        put(priceUpdate.symbol, priceUpdate)
                    }
                    val sortedList = updatedMap.values.sortedByDescending { it.price }
                    currentState.copy(
                        priceUpdates = updatedMap,
                        sortedUpdates = sortedList
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.connectionState.collect { state ->
                _uiState.update { it.copy(connectionState = state) }
            }
        }
    }

    fun toggleConnection() {
        if (repository.isConnected()) {
            repository.disconnect()
            _uiState.update {
                it.copy(
                    priceUpdates = emptyMap(),
                    sortedUpdates = emptyList()
                )
            }
        } else {
            repository.connect(StockSymbols.ALL_SYMBOLS)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
