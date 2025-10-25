package com.dpm.sixpack.presentation.routes.terms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TermsSideEffect.NavigateToSignUp -> onNavigateToSignUp()
            is TermsSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    TermsAgreementScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
