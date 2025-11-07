package com.dpm.sixpack.presentation.routes.report.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.destinations.SessionReportRoute
import com.dpm.sixpack.presentation.routes.report.ReportRoute

fun NavController.navigateSessionReport(
    sessionId: Long,
    navOptions: NavOptions? = null,
) {
    navigate(SessionReportRoute(sessionId), navOptions)
}

fun NavGraphBuilder.addSessionReportNavGraph(
    navigateToBack: () -> Unit,
    onShowSnackBar: (String, String?) -> Unit,
    navigateToCertification: (Long) -> Unit = {},
) {
    composable<SessionReportRoute> { backStackEntry ->
        val sessionId: Long = backStackEntry.toRoute<SessionReportRoute>().sessionId

        ReportRoute(
            sessionId = sessionId,
            navigateToBack = navigateToBack,
            navigateToCertification = {
//                navigateToCertification(sessionId)
            },
            onShowSnackBar = onShowSnackBar,
        )
    }
}
