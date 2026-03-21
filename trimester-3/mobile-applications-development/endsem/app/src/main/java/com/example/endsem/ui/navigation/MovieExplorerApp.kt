package com.example.endsem.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.endsem.ui.screens.CartScreen
import com.example.endsem.ui.screens.MovieExplorerScreen
import com.example.endsem.ui.screens.ProfileScreen
import com.example.endsem.ui.screens.RentalScreen
import com.example.endsem.ui.screens.WishlistScreen
import com.example.endsem.viewmodel.MovieViewModel

@Composable
fun MovieExplorerApp(
    viewModel: MovieViewModel
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val rentalsState by viewModel.rentalsUiState.collectAsState()
    val cartState by viewModel.cartUiState.collectAsState()
    val wishlistState by viewModel.wishlistUiState.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            MovieExplorerBottomBar(
                currentRoute = currentRoute,
                cartCount = cartState.cartItems.size,
                rentalCount = rentalsState.rentals.size,
                wishlistCount = wishlistState.items.size,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.MovieExplorer.route
            ) {
                composable(Screen.MovieExplorer.route) {
                    MovieExplorerScreen(viewModel = viewModel)
                }
                composable(Screen.Cart.route) {
                    CartScreen(viewModel = viewModel)
                }
                composable(Screen.Wishlist.route) {
                    WishlistScreen(viewModel = viewModel)
                }
                composable(Screen.Rentals.route) {
                    RentalScreen(viewModel = viewModel)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun MovieExplorerBottomBar(
    currentRoute: String?,
    cartCount: Int,
    rentalCount: Int,
    wishlistCount: Int,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val badgeCount = when (screen) {
                Screen.Cart -> cartCount
                Screen.Rentals -> rentalCount
                Screen.Wishlist -> wishlistCount
                else -> 0
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    if (badgeCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ) {
                                    Text(text = badgeCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                            contentDescription = screen.title
                        )
                    }
                },
                label = { Text(text = screen.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
