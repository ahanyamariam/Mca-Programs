package com.example.lab5.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab5.ui.screens.SearchScreen
import com.example.lab5.ui.screens.WeatherDetailScreen
import com.example.lab5.ui.viewmodel.WeatherViewModel
import java.net.URLDecoder
import java.net.URLEncoder

object Routes {
    const val SEARCH = "search"
    const val DETAIL = "detail/{cityId}"

    fun detailRoute(cityId: Long): String {
        return "detail/$cityId"
    }
}

@Composable
fun WeatherNavigation() {
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH
    ) {
        composable(Routes.SEARCH) {
            SearchScreen(
                viewModel = viewModel,
                onCitySelected = { cityId ->
                    viewModel.loadWeatherDetails(cityId)
                    navController.navigate(Routes.detailRoute(cityId))
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("cityId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            WeatherDetailScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
