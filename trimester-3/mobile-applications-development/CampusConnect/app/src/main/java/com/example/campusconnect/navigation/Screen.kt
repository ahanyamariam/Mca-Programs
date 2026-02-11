package com.example.campusconnect.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
    object Departments : Screen("departments")
    object EventDetails : Screen("event_details/{departmentName}") {
        fun createRoute(departmentName: String) = "event_details/$departmentName"
    }
}