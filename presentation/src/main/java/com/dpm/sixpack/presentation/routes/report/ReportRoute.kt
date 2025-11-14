package com.dpm.sixpack.presentation.routes.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.routes.report.contract.ReportSideEffect
import com.dpm.sixpack.presentation.routes.report.screen.SessionReportScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ReportRoute(
    viewModel: SessionReportViewModel = hiltViewModel(),
    navigateToHome: () -> Unit = {},
    navigateToBack: () -> Unit = {},
    navigateToPostUpload: (Long, String, RunningSummary) -> Unit = { _, _, _ -> },
    navigateToPostDetail: (Long) -> Unit = {},
    onShowSnackBar: (String, String?) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            ReportSideEffect.NavigateBack -> navigateToBack()
            is ReportSideEffect.NavigateToPostUpload ->
                navigateToPostUpload(
                    sideEffect.sessionId,
                    sideEffect.mapImageUrl,
                    sideEffect.runningSummary,
                )

            is ReportSideEffect.ShowSnackBar -> {
                val message = context.getString(sideEffect.resId)
                onShowSnackBar(message, null)
            }

            is ReportSideEffect.NavigateToPostDetail -> navigateToPostDetail(sideEffect.feedId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(ReportIntent.LoadSessionDetail)
    }

    SessionReportScreen(
        state = state.value,
        onIntent = viewModel::onIntent,
        navigateToHome = navigateToHome,
    )
}
