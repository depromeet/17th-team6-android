package com.dpm.sixpack.presentation.routes.sessionreport.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SessionReportRoute
import com.dpm.sixpack.presentation.routes.sessionreport.SessionReportRoute

fun NavController.navigateSessionReport(navOptions: NavOptions? = null) {
    navigate(SessionReportRoute, navOptions)
}

fun NavGraphBuilder.addSessionReportNavGraph(onNavigateToBack: () -> Unit) {
    composable<SessionReportRoute> {
        SessionReportRoute(
            navigateBack = onNavigateToBack,
        )
    }
}
