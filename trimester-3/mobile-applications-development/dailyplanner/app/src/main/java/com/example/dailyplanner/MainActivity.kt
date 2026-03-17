package com.example.dailyplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dailyplanner.navigation.Screen
import com.example.dailyplanner.navigation.bottomNavItems
import com.example.dailyplanner.ui.budget.BudgetScreen
import com.example.dailyplanner.ui.expense.ExpenseScreen
import com.example.dailyplanner.ui.habit.HabitScreen
import com.example.dailyplanner.ui.planner.PlannerScreen
import com.example.dailyplanner.ui.pomodoro.PomodoroScreen
import com.example.dailyplanner.ui.theme.DailyplannerTheme
import com.example.dailyplanner.ui.todo.TodoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyplannerTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                screen.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Planner.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(Screen.Planner.route) { PlannerScreen() }
            composable(Screen.Todo.route) { TodoScreen() }
            composable(Screen.Habits.route) { HabitScreen() }
            composable(Screen.Focus.route) { PomodoroScreen() }
            composable(Screen.Expenses.route) { ExpenseScreen() }
            composable(Screen.Budget.route) { BudgetScreen() }
        }
    }
}