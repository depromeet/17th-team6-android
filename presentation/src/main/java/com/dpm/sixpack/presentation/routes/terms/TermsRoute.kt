package com.dpm.sixpack.presentation.routes.terms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.common.util.context.openUrlInBrowser
import com.dpm.sixpack.presentation.routes.terms.contract.TermsSideEffect
import com.dpm.sixpack.presentation.routes.terms.ui.screen.TermsAgreementScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun TermsRoute(
    modifier: Modifier = Modifier,
    viewModel: TermsViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TermsSideEffect.NavigateToSignUp -> onNavigateToSignUp()
            is TermsSideEffect.NavigateBack -> onNavigateBack()
            is TermsSideEffect.OpenTermUrl -> context.openUrlInBrowser(sideEffect.url)
        }
    }

    TermsAgreementScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
