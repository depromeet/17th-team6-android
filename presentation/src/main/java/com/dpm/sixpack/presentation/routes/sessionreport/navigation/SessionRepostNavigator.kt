package com.dpm.sixpack.presentation.routes.sessionreport.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.RunningRoute
import com.dpm.sixpack.presentation.routes.sessionreport.SessionReportRoute

fun NavController.navigateSessionReport(navOptions: NavOptions? = null) {
    navigate(RunningRoute.Report, navOptions)
}

fun NavGraphBuilder.addSessionReportNavGraph(onNavigateToBack: () -> Unit) {
    composable<RunningRoute.Report> {
        SessionReportRoute(
            navigateBack = onNavigateToBack,
        )
    }
}
