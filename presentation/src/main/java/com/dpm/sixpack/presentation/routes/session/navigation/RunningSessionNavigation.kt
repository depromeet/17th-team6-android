package com.dpm.sixpack.presentation.routes.session.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.routes.MainRoute
import com.dpm.sixpack.presentation.routes.session.RunningSessionRoute

fun NavController.navigateRunningSession(navOptions: NavOptions? = null) {
    navigate(MainRoute.Session, navOptions)
}

fun NavGraphBuilder.addRunningSessionNavGraph() {
    composable<MainRoute.Session> {
        RunningSessionRoute(
            // fill
        )
    }
}
