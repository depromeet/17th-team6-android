package com.dpm.sixpack.presentation.routes.session

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun RunningSessionRoute(
    modifier: Modifier = Modifier,
    viewModel: RunningSessionViewModel,
) {
    val uiState = viewModel.collectAsState()
    viewModel.collectSideEffect { }
}
