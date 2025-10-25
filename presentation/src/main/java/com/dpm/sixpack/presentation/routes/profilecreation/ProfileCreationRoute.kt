package com.dpm.sixpack.presentation.routes.profilecreation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationSideEffect
import com.dpm.sixpack.presentation.routes.profilecreation.ui.screen.ProfileCreationScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileCreationRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileCreationViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileCreationSideEffect.NavigateToHome -> onNavigateToHome()
            is ProfileCreationSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    ProfileCreationScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
