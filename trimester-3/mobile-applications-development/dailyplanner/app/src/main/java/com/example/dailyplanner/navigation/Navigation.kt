package com.example.dailyplanner.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Planner : Screen("planner", "Planner", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Todo : Screen("todo", "To-Do", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle)
    object Habits : Screen("habits", "Habits", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp)
    object Focus : Screen("focus", "Focus", Icons.Filled.Timer, Icons.Outlined.Timer)
    object Expenses : Screen("expenses", "Expenses", Icons.Filled.Receipt, Icons.Outlined.Receipt)
    object Budget : Screen("budget", "Budget", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance)
}

val bottomNavItems = listOf(
    Screen.Planner,
    Screen.Todo,
    Screen.Habits,
    Screen.Focus,
    Screen.Expenses,
    Screen.Budget
)
