package com.dpm.sixpack.presentation.routes.running.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.running.RunningRoute

fun NavController.navigateRunningSession(navOptions: NavOptions? = null) {
    navigate(MainRoute.Running, navOptions)
}

fun NavGraphBuilder.addRunningSessionNavGraph(
    onNavigateToBack: () -> Unit,
    navigateToSessionReport: () -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {
    composable<MainRoute.Running> {
        RunningRoute(
            onNavigateToBack = onNavigateToBack,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            navigateToReport = navigateToSessionReport,
        )
    }
}
