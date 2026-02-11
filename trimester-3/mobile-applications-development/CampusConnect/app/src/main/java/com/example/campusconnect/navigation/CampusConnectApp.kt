package com.example.campusconnect.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.campusconnect.ui.screens.*
import kotlinx.coroutines.launch

// Bottom Navigation Items
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home)
    object Notifications : BottomNavItem(Screen.Notifications.route, "Notifications", Icons.Default.Notifications)
    object Profile : BottomNavItem(Screen.Profile.route, "Profile", Icons.Default.Person)
}

// Drawer Menu Items
sealed class DrawerMenuItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : DrawerMenuItem(Screen.Home.route, "Home", Icons.Default.Home)
    object Departments : DrawerMenuItem(Screen.Departments.route, "Departments", Icons.Default.AccountBalance)
    object Profile : DrawerMenuItem(Screen.Profile.route, "Profile", Icons.Default.Person)
    object Logout : DrawerMenuItem("logout", "Logout", Icons.Default.ExitToApp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusConnectApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )

    val drawerMenuItems = listOf(
        DrawerMenuItem.Home,
        DrawerMenuItem.Departments,
        DrawerMenuItem.Profile,
        DrawerMenuItem.Logout
    )

    // Track current screen to show/hide bottom navigation
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Screens where bottom navigation should be visible
    val bottomNavScreens = listOf(
        Screen.Home.route,
        Screen.Notifications.route,
        Screen.Profile.route
    )

    val showBottomNav = currentDestination?.route in bottomNavScreens

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                DrawerContent(
                    menuItems = drawerMenuItems,
                    onItemClick = { item ->
                        scope.launch {
                            drawerState.close()
                            if (item.route == "logout") {
                                // Handle logout
                                // For now, just navigate to home
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    },
                    currentRoute = currentDestination?.route
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true

                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavigationGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onMenuClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}

@Composable
fun DrawerContent(
    menuItems: List<DrawerMenuItem>,
    onItemClick: (DrawerMenuItem) -> Unit,
    currentRoute: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "Campus Connect",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Menu Items
        menuItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMenuClick = onMenuClick,
                onNavigateToDepartments = {
                    navController.navigate(Screen.Departments.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen()
        }

        composable(Screen.Departments.route) {
            DepartmentsScreen(
                onDepartmentClick = { departmentName ->
                    navController.navigate(Screen.EventDetails.createRoute(departmentName))
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.EventDetails.route) { backStackEntry ->
            val departmentName = backStackEntry.arguments?.getString("departmentName") ?: ""
            EventDetailsScreen(
                departmentName = departmentName,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
    }
}