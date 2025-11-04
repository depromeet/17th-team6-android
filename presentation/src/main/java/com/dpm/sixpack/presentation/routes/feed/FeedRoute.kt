package com.dpm.sixpack.presentation.routes.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.ui.FeedScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel(),
    navigateToGroup: () -> Unit,
    navigateToAlarm: () -> Unit,
    navigateToCertifiedUserList: () -> Unit,
    navigateToUserProfile: (Long) -> Unit,
    navigateToMyPage: () -> Unit,
    navigateToPostDetail: (PostResource) -> Unit,
    navigateToPostUpload: () -> Unit,
    navigateToPostEdit: (PostResource) -> Unit,
) {
    val state by viewModel.collectAsState()
    val feedPagingItems = viewModel.feedPagingData.collectAsLazyPagingItems()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FeedSideEffect.NavigateToFriend -> navigateToGroup()
            is FeedSideEffect.NavigateToAlarm -> navigateToAlarm()
            is FeedSideEffect.NavigateToCertificationFriend -> navigateToCertifiedUserList()
            is FeedSideEffect.NavigateToMyPage -> navigateToMyPage()
            is FeedSideEffect.NavigateToUserPage -> navigateToUserProfile(sideEffect.userId)
            is FeedSideEffect.NavigateToPostDetail -> navigateToPostDetail(sideEffect.post)
            is FeedSideEffect.NavigateToPostUpload -> navigateToPostUpload()
            is FeedSideEffect.NavigateToPostEdit -> navigateToPostEdit(sideEffect.post)
            is FeedSideEffect.ShowToast -> {
                // TODO: Show toast
            }
            is FeedSideEffect.RefreshPagingList -> feedPagingItems.refresh()
        }
    }

    FeedScreen(
        state = state,
        feedPagingItems = feedPagingItems,
        onIntent = viewModel::onIntent,
    )
}
