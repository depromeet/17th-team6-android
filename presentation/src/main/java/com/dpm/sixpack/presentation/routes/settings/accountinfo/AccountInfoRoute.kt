package com.dpm.sixpack.presentation.routes.settings.accountinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoSideEffect
import com.dpm.sixpack.presentation.routes.settings.accountinfo.ui.screen.AccountInfoScreen

@Composable
fun AccountInfoRoute(
    viewModel: AccountInfoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.container.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                AccountInfoSideEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    AccountInfoScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
