package com.dpm.sixpack.presentation.routes.deprecated.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.deprecated.home.HomeRoute

@Deprecated("Home 변경")
fun NavController.navigateHome(navOptions: NavOptions? = null) {
    navigate(MainRoute.Home, navOptions)
}

@Deprecated("Home 변경")
fun NavGraphBuilder.addHomeNavGraph(
    onNavigateToSessionList: (goalId: Long) -> Unit,
    onNavigateToSession: (sessionId: Long) -> Unit,
    onNavigateToGoalEdit: () -> Unit,
) {
    composable<MainRoute.Home> {
        HomeRoute(
            onNavigateToSessionList = onNavigateToSessionList,
            onNavigateToSession = onNavigateToSession,
            onNavigateToGoalEdit = onNavigateToGoalEdit,
        )
    }
}
