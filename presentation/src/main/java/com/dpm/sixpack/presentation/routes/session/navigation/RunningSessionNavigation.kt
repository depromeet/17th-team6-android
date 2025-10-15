package com.dpm.sixpack.presentation.routes.session.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.session.RunningSessionRoute

fun NavController.navigateRunningSession(navOptions: NavOptions? = null) {
    navigate(MainRoute.Running, navOptions)
}

fun NavGraphBuilder.addRunningSessionNavGraph(
    onNavigateToBack: () -> Unit,
    navigateToSessionReport: () -> Unit = {},
) {
    composable<MainRoute.Running> {
        RunningSessionRoute(
            onNavigateToBack = onNavigateToBack,
            navigateToSessionReport = navigateToSessionReport,
        )
    }
}
