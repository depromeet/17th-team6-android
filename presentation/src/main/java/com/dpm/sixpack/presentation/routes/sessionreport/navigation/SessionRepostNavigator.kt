package com.dpm.sixpack.presentation.routes.sessionreport.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.dpm.sixpack.presentation.destinations.SessionReportRoute

fun NavController.navigateSessionReport(sessionId: Long, navOptions: NavOptions? = null) {
    navigate(SessionReportRoute(sessionId), navOptions)
}

fun NavGraphBuilder.addSessionReportNavGraph(onNavigateToBack: () -> Unit) {
    composable<SessionReportRoute> { backStackEntry ->
        val sessionId: Long = backStackEntry.toRoute<SessionReportRoute>().sessionId

//        RunningRecordDetailScreen(
//            navigateToBack = onNavigateToBack,
//            sessionDetail = TODO(),
//            onNavigateBack = TODO(),
//            onNavigateToCertification = TODO(),
//        )
    }
}
