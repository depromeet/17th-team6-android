package com.dpm.sixpack.presentation.routes.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.RunningRoute
import com.dpm.sixpack.presentation.routes.home.HomeRoute

fun NavController.navigateHome(navOptions: NavOptions? = null) {
    navigate(RunningRoute.Home, navOptions)
}

fun NavGraphBuilder.addHomeNavGraph(
    onNavigateToSession: (sessionId: Long) -> Unit,
    onNavigateToGoalList: (goalId: Long) -> Unit,
    onNavigateToGoalEdit: () -> Unit
) {
    composable<RunningRoute.Home> {
        HomeRoute(
            onNavigateToSession = onNavigateToSession,
            onNavigateToGoalList = onNavigateToGoalList,
            onNavigateToGoalEdit = onNavigateToGoalEdit
        )
    }
}
