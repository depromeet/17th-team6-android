package com.dpm.sixpack.presentation.routes.session

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun RunningSessionRoute(
    modifier: Modifier = Modifier,
    viewModel: RunningSessionViewModel = hiltViewModel(),
) {
    val uiState = viewModel.collectAsState()
    viewModel.collectSideEffect { }
}
