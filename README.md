# Real-Time Price Tracker App

This is my submission for the Android coding challenge. The app tracks real-time stock prices using WebSockets and is built entirely with Jetpack Compose.

## What It Does

The app displays a scrollable list of 25 stock symbols with live price updates. Each symbol gets a random price update every 2 seconds through a WebSocket connection to Postman's echo server. You can tap any stock to see more details about it.

The main feed screen shows all stocks sorted by price (highest first), with green/red indicators showing whether the price went up or down. There's also a connection status dot and a start/stop button at the top to control the price feed.

## Implementation Details

**Architecture**: I went with MVVM since it's what I'm most comfortable with and it keeps things organized. The ViewModels handle the business logic and WebSocket management, while the UI just observes state changes.

**WebSocket**: Using OkHttp's WebSocket client. The app sends generated price updates for each symbol every 2 seconds, receives the echo back, and updates the UI accordingly. I made sure there's only one WebSocket connection that both screens can observe.

**Navigation**: Set up with Navigation Compose - two destinations (feed and details). The details screen uses SavedStateHandle to get the selected symbol from the nav arguments.

**UI**: Built everything with Compose - LazyColumn for the scrollable list, custom composables for the stock items and price indicators. Tried to keep the UI clean and responsive.

## Tech Stack

- Kotlin
- Jetpack Compose (100% of the UI)
- Navigation Compose
- StateFlow for reactive state management
- OkHttp for WebSocket connection
- Coroutines for async operations
- Material Design 3

## How to Run

1. Clone the repo
2. Open in Android Studio
3. Let Gradle sync
4. Run on emulator or device (min SDK 24)

The WebSocket connects automatically when you start the feed.

## Notes

- The 25 stock symbols are hardcoded with some basic info about each company
- Price updates are randomly generated between -5% and +5% of current price
- Used Kotlin Flows throughout for handling the data stream
- Kept state immutable and used data classes for everything
- The connection can be started/stopped from the top bar

