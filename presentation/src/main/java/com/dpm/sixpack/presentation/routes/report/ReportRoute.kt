package com.dpm.sixpack.presentation.routes.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.routes.report.contract.ReportSideEffect
import com.dpm.sixpack.presentation.routes.report.screen.SessionReportScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ReportRoute(
    sessionId: Long,
    viewModel: SessionReportViewModel = hiltViewModel(),
    navigateToBack: () -> Unit = {},
    navigateToCertification: () -> Unit = {},
    onShowSnackBar: (String, String?) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            ReportSideEffect.NavigateBack -> navigateToBack()
            is ReportSideEffect.NavigateToPostEdit -> navigateToCertification()
            is ReportSideEffect.ShowToast -> {
                val message = context.getString(sideEffect.resId)
                onShowSnackBar(message, null)
            }
        }
    }

    LaunchedEffect(sessionId) {
        viewModel.onIntent(ReportIntent.LoadSessionDetail(sessionId))
    }

    SessionReportScreen(
        sessionId = sessionId,
        state = state.value,
        onIntent = viewModel::onIntent,
    )
}
