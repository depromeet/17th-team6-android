package com.dpm.sixpack.presentation.routes.session.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.routes.Route
import com.dpm.sixpack.presentation.routes.session.RunningSessionRoute

fun NavController.navigateRunning(navOptions: NavOptions? = null) {
    navigate(Route.Running, navOptions)
}

fun NavGraphBuilder.addRunningNavGraph() {
    composable<Route.Running> {
        RunningSessionRoute(

        )
    }
}
