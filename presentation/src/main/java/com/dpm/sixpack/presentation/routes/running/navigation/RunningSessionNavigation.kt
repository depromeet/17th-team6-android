package com.dpm.sixpack.presentation.routes.running.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.MainRoute
import com.dpm.sixpack.presentation.routes.running.RunningRoute

fun NavController.navigateRunning(navOptions: NavOptions? = null) {
    navigate(MainRoute.Running, navOptions)
}

fun NavGraphBuilder.addRunningNavGraph() {
    composable<MainRoute.Running> {
        RunningRoute(
            // fill
        )
    }
}
