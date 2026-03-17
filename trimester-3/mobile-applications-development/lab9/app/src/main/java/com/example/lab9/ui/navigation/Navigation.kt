package com.example.lab9.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lab9.ui.screen.*
import com.example.lab9.ui.theme.IndigoAccent
import com.example.lab9.ui.theme.NavyCard
import com.example.lab9.ui.theme.TextPrimary
import com.example.lab9.ui.theme.TextSecondary
import com.example.lab9.ui.viewmodel.SyncViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Schedule : Screen("schedule", "Schedule", Icons.Default.CalendarMonth)
    object Announcements : Screen("announcements", "Notices", Icons.Default.Notifications)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavScreens = listOf(Screen.Home, Screen.Schedule, Screen.Announcements, Screen.Settings)

@Composable
fun PortalNavigation(viewModel: SyncViewModel) {
    val navController = rememberNavController()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = NavyCard,
                contentColor = TextPrimary
            ) {
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            if (screen is Screen.Announcements && unreadCount > 0) {
                                BadgedBox(badge = {
                                    Badge { Text(if (unreadCount > 9) "9+" else unreadCount.toString()) }
                                }) {
                                    Icon(screen.icon, contentDescription = screen.label)
                                }
                            } else {
                                Icon(screen.icon, contentDescription = screen.label)
                            }
                        },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoAccent,
                            selectedTextColor = IndigoAccent,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = IndigoAccent.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModel) }
            composable(Screen.Schedule.route) { ScheduleScreen(viewModel) }
            composable(Screen.Announcements.route) { AnnouncementsScreen(viewModel) }
            composable(Screen.Settings.route) { SettingsScreen(viewModel) }
        }
    }
}
