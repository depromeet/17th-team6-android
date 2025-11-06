package com.dpm.sixpack.presentation.routes.feed.certifiedusers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract.CertifiedUsersSideEffect
import com.dpm.sixpack.presentation.routes.feed.certifiedusers.ui.CertifiedUsersScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CertifiedUsersRoute(
    navigateToBack: () -> Unit,
    navigateToUserProfile: (Long) -> Unit,
    navigateToMyPage: () -> Unit,
    viewModel: CertifiedUsersViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            CertifiedUsersSideEffect.NavigateBack -> navigateToBack()
            CertifiedUsersSideEffect.NavigateToMyPage -> navigateToMyPage()
            is CertifiedUsersSideEffect.NavigateToUserPage ->
                navigateToUserProfile(sideEffect.userId)
        }
    }

    CertifiedUsersScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
