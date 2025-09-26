package com.dpm.sixpack.presentation.routes.session.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.RunningRoute
import com.dpm.sixpack.presentation.routes.session.RunningSessionRoute

fun NavController.navigateRunningSession(navOptions: NavOptions? = null) {
    navigate(RunningRoute.Session, navOptions)
}

fun NavGraphBuilder.addRunningSessionNavGraph(onNavigateToBack: () -> Unit) {
    composable<RunningRoute.Session> {
        RunningSessionRoute(
            onNavigateToBack = onNavigateToBack,
        )
    }
}
