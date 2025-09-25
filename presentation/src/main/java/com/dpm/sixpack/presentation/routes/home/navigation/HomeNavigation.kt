package com.dpm.sixpack.presentation.routes.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.home.HomeRoute

fun NavController.navigateHome(navOptions: NavOptions? = null) {
    navigate(MainRoute.Home, navOptions)
}

fun NavGraphBuilder.addHomeNavGraph(
    onNavigateToSession: (sessionId: Long) -> Unit,
    onNavigateToGoalList: (goalId: Long) -> Unit,
    onNavigateToGoalEdit: () -> Unit,
) {
    composable<MainRoute.Home> {
        HomeRoute(
            onNavigateToSession = onNavigateToSession,
            onNavigateToGoalList = onNavigateToGoalList,
            onNavigateToGoalEdit = onNavigateToGoalEdit,
        )
    }
}
