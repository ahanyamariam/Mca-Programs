package com.example.cia3.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cia3.ui.screens.AddEditTaskScreen
import com.example.cia3.ui.screens.ManageScreen
import com.example.cia3.ui.screens.SettingsScreen
import com.example.cia3.ui.screens.TaskListScreen
import com.example.cia3.viewmodel.SettingsViewModel
import com.example.cia3.viewmodel.TaskViewModel

// Navigation routes
object Routes {
    const val TASK_LIST = "task_list"
    const val MANAGE = "manage"
    const val ADD_TASK = "add_task"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val SETTINGS = "settings"

    fun editTask(taskId: Int) = "edit_task/$taskId"
}

// Bottom nav items
data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerApp(
    taskViewModel: TaskViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Settings values
    val userName by settingsViewModel.userName.collectAsState()
    val hideDescriptions by settingsViewModel.hideDescriptions.collectAsState()
    val confirmDelete by settingsViewModel.confirmDelete.collectAsState()
    val sortOrder by settingsViewModel.sortOrder.collectAsState()
    val viewFilter by settingsViewModel.viewFilter.collectAsState()

    val bottomNavItems = remember {
        listOf(
            BottomNavItem(
                label = "Tasks",
                selectedIcon = Icons.Filled.TaskAlt,
                unselectedIcon = Icons.Outlined.TaskAlt,
                route = Routes.TASK_LIST
            ),
            BottomNavItem(
                label = "Manage",
                selectedIcon = Icons.Outlined.Build,
                unselectedIcon = Icons.Outlined.Build,
                route = Routes.MANAGE
            )
        )
    }

    val isMainScreen = currentRoute == Routes.TASK_LIST || currentRoute == Routes.MANAGE

    Scaffold(
        topBar = {
            if (isMainScreen) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Task Manager",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Routes.SETTINGS)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            if (isMainScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.TASK_LIST) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (isMainScreen) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.ADD_TASK)
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task"
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.TASK_LIST,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Routes.TASK_LIST) {
                TaskListScreen(
                    viewModel = taskViewModel,
                    onEditTask = { task ->
                        navController.navigate(Routes.editTask(task.taskId))
                    },
                    userName = userName,
                    hideDescriptions = hideDescriptions,
                    confirmDelete = confirmDelete,
                    sortOrder = sortOrder,
                    viewFilter = viewFilter
                )
            }

            composable(Routes.MANAGE) {
                ManageScreen(
                    viewModel = taskViewModel,
                    confirmDelete = confirmDelete
                )
            }

            composable(Routes.ADD_TASK) {
                AddEditTaskScreen(
                    viewModel = taskViewModel,
                    taskToEdit = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.EDIT_TASK,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
                val tasks by taskViewModel.allTasks.collectAsState()
                val taskToEdit = tasks.find { it.taskId == taskId }

                if (taskToEdit != null) {
                    AddEditTaskScreen(
                        viewModel = taskViewModel,
                        taskToEdit = taskToEdit,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
