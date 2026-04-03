package com.example.realtimepricetrackerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.realtimepricetrackerapp.ui.screens.details.DetailsScreen
import com.example.realtimepricetrackerapp.ui.screens.details.DetailsViewModel
import com.example.realtimepricetrackerapp.ui.screens.feed.FeedScreen

sealed class Screen(val route: String) {
    data object Feed : Screen("feed")
    data object Details : Screen("details/{symbol}") {
        fun createRoute(symbol: String) = "details/$symbol"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route
    ) {
        composable(Screen.Feed.route) {
            FeedScreen(
                onNavigateToDetails = { symbol ->
                    navController.navigate(Screen.Details.createRoute(symbol))
                }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("symbol") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            DetailsScreen(
                viewModel = viewModel(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
