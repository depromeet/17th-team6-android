package com.dpm.sixpack.presentation.routes.sessionreport.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SessionReportRoute

fun NavController.navigateSessionReport(navOptions: NavOptions? = null) {
    navigate(SessionReportRoute, navOptions)
}

fun NavGraphBuilder.addSessionReportNavGraph(onNavigateToBack: () -> Unit) {
    composable<SessionReportRoute> { backStackEntry ->
//        val sessionId: Long = backStackEntry.toRoute<SessionReportRoute>().sessionId

//        RunningRecordDetailScreen(
//            navigateToBack = onNavigateToBack,
//            sessionDetail = TODO(),
//            onNavigateBack = TODO(),
//            onNavigateToCertification = TODO(),
//        )
    }
}
