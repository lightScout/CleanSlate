package org.lightscout.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.lightscout.presentation.taskdetail.TaskDetailScreen
import org.lightscout.presentation.tasklist.TaskListScreen

sealed class Screen(val route: String) {
    data object TaskList : Screen("taskList")
    data object TaskDetail : Screen("taskDetail/{taskId}") {
        fun createRoute(taskId: String) = "taskDetail/$taskId"
    }
}

@Composable
fun AppNavigation(
        navController: NavHostController,
        startDestination: String = Screen.TaskList.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                    onNavigateToTaskDetail = { taskId ->
                        navController.navigate(Screen.TaskDetail.createRoute(taskId))
                    }
            )
        }

        composable(Screen.TaskDetail.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(taskId = taskId, onNavigateBack = { navController.popBackStack() })
        }
    }
}
