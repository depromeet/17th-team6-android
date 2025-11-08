package com.dpm.sixpack.presentation.routes.feed.certifiablerecord

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.contract.CertifiableRecordSideEffect
import com.dpm.sixpack.presentation.routes.feed.certifiablerecord.ui.CertifiableRecordScreen
import com.dpm.sixpack.presentation.theme.SixpackTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CertifiableRecordRoute(
    viewModel: CertifiableRecordViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToPostUpload: (Long, String, RunningSummary) -> Unit,
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            CertifiableRecordSideEffect.NavigateBack -> navigateToBack()
            is CertifiableRecordSideEffect.NavigateToPostUpload -> {
                val record = state.selectedRecord ?: return@collectSideEffect
                navigateToPostUpload(record.sessionId, record.mapImageUrl, record.runningSummary)
            }

            CertifiableRecordSideEffect.ShowNoRecordSelectedError -> TODO()
        }
    }

    if (state.isLoading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = SixpackTheme.colors.blue600,
            )
        }
    }
    CertifiableRecordScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
