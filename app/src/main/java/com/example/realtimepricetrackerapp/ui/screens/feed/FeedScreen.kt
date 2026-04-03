package com.example.realtimepricetrackerapp.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.realtimepricetrackerapp.data.model.ConnectionState
import com.example.realtimepricetrackerapp.ui.components.StockListItem
import com.example.realtimepricetrackerapp.util.StockSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel(),
    onNavigateToDetails: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val connectionEmoji = when (uiState.connectionState) {
        is ConnectionState.Connected -> "🟢"
        is ConnectionState.Connecting -> "🟡"
        is ConnectionState.Disconnected -> "🔴"
        is ConnectionState.Error -> "🔴"
    }

    val buttonText = when (uiState.connectionState) {
        is ConnectionState.Connected -> "Stop"
        else -> "Start"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("$connectionEmoji Real-Time Prices")
                },
                actions = {
                    Button(onClick = { viewModel.toggleConnection() }) {
                        Text(buttonText)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.sortedUpdates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Press Start to begin tracking prices",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(
                    items = uiState.sortedUpdates,
                    key = { it.symbol }
                ) { priceUpdate ->
                    val stockSymbol = StockSymbols.ALL_SYMBOLS.find { it.symbol == priceUpdate.symbol }
                    StockListItem(
                        priceUpdate = priceUpdate,
                        stockSymbol = stockSymbol,
                        onClick = { onNavigateToDetails(priceUpdate.symbol) }
                    )
                }
            }
        }
    }
}
