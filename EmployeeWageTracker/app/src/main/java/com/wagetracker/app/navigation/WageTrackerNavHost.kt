package com.wagetracker.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wagetracker.app.repository.WageRepository
import com.wagetracker.app.ui.screens.DashboardScreen
import com.wagetracker.app.ui.screens.EmployeeDetailScreen
import com.wagetracker.app.viewmodel.EmployeeDetailViewModel
import com.wagetracker.app.viewmodel.MainViewModel
import java.net.URLDecoder
import java.net.URLEncoder

object Routes {
    const val DASHBOARD = "dashboard"
    const val EMPLOYEE_DETAIL = "employee/{employeeId}/{employeeName}"
    fun employeeDetail(employeeId: Long, employeeName: String): String {
        val encoded = URLEncoder.encode(employeeName, "UTF-8")
        return "employee/$employeeId/$encoded"
    }
}

@Composable
fun WageTrackerNavHost(repository: WageRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(repository))
            DashboardScreen(
                viewModel = viewModel,
                onEmployeeClick = { employee ->
                    navController.navigate(
                        Routes.employeeDetail(employee.id, employee.name)
                    )
                }
            )
        }
        composable(
            route = Routes.EMPLOYEE_DETAIL,
            arguments = listOf(
                navArgument("employeeId") { type = NavType.LongType },
                navArgument("employeeName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getLong("employeeId") ?: 0L
            val rawName = backStackEntry.arguments?.getString("employeeName") ?: ""
            val employeeName = URLDecoder.decode(rawName, "UTF-8")
            val viewModel: EmployeeDetailViewModel = viewModel(
                factory = EmployeeDetailViewModel.Factory(repository, employeeId)
            )
            EmployeeDetailScreen(
                employeeName = employeeName,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
