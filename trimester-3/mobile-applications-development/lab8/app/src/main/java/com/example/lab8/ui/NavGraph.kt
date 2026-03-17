package com.example.lab8.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(viewModel: AttendeeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "registration") {
        composable("registration") {
            RegistrationScreen(
                viewModel = viewModel,
                onNavigateToManagement = { navController.navigate("management") }
            )
        }
        composable("management") {
            ManagementScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
