package com.dpm.sixpack.presentation.routes.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.feed.ui.screen.FeedScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateToBack: () -> Unit,
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            // TODO: Handle side effects
            else -> {}
        }
    }

    FeedScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
    )
}
