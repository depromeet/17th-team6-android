package com.dpm.sixpack.presentation.routes.feed.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState

@Composable
fun FeedScreen(
    state: FeedUiState,
    onIntent: (FeedIntent) -> Unit,
) {
    // TODO: Implement Feed UI based on the state
    Text(text = "Feed Screen")
}

@Preview
@Composable
private fun FeedScreenPreview() {
    // DoRunPreviewWrapper {
        FeedScreen(
            state = FeedUiState(),
            onIntent = {},
        )
    // }
}
