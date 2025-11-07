package com.dpm.sixpack.presentation.routes.sessionreport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.sessionreport.contract.SessionDetailIntent
import com.dpm.sixpack.presentation.routes.sessionreport.contract.SessionDetailSideEffect
import com.dpm.sixpack.presentation.routes.sessionreport.screen.SessionDetailScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SessionDetailRoute(
    sessionId: Long,
    viewModel: SessionDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit = {},
    navigateToCertification: () -> Unit = {},
    onShowSnackBar: (String, String?) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            SessionDetailSideEffect.NavigateBack -> navigateToBack()
            is SessionDetailSideEffect.NavigateToCertification -> navigateToCertification()
            is SessionDetailSideEffect.ShowToast -> {
                val message = context.getString(sideEffect.resId)
                onShowSnackBar(message, null)
            }
        }
    }

    LaunchedEffect(sessionId) {
        viewModel.onIntent(SessionDetailIntent.LoadSessionDetail(sessionId))
    }

    SessionDetailScreen(
        sessionId = sessionId,
        state = state.value,
        onIntent = viewModel::onIntent,
    )
}
