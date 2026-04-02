package com.example.realtimepricetrackerapp.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realtimepricetrackerapp.data.model.PriceUpdate
import com.example.realtimepricetrackerapp.data.model.StockSymbol
import com.example.realtimepricetrackerapp.data.websocket.StockDataRepository
import com.example.realtimepricetrackerapp.util.StockSymbols
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailsUiState(
    val stockSymbol: StockSymbol? = null,
    val priceUpdate: PriceUpdate? = null
)

class DetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository = StockDataRepository()
    private val symbol: String = checkNotNull(savedStateHandle["symbol"])

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        val stockSymbol = StockSymbols.ALL_SYMBOLS.find { it.symbol == symbol }
        _uiState.update { it.copy(stockSymbol = stockSymbol) }

        viewModelScope.launch {
            repository.priceUpdates.collect { priceUpdate ->
                if (priceUpdate.symbol == symbol) {
                    _uiState.update { it.copy(priceUpdate = priceUpdate) }
                }
            }
        }
    }
}
