package com.dpm.sixpack.presentation.routes.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.ui.screen.FeedScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateToGroup: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    onNavigateToCertifiedUserList: () -> Unit,
    onNavigateToUserProfile: (Int) -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToPostDetail: (Int) -> Unit,
) {
    val state by viewModel.collectAsState()
    val feedPagingItems = viewModel.feedPagingFlow.collectAsLazyPagingItems()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FeedSideEffect.ShowMenuBalloon -> {
                // TODO: Show menu balloon
            }
            is FeedSideEffect.ShowToast -> {
                // TODO: Show toast
            }
        }
    }

    FeedScreen(
        state = state,
        feedPagingItems = feedPagingItems,
        onTopBarGroupIconClick = { viewModel.onIntent(FeedIntent.OnTopBarGroupIconClick) },
        onTopBarAlarmIconClick = { viewModel.onIntent(FeedIntent.OnTopBarAlarmIconClick) },
        onDateSelected = { date -> viewModel.onIntent(FeedIntent.OnDateSelected(date)) },
        onVisibleWeeksChanged = { startDate -> viewModel.onIntent(FeedIntent.OnVisibleWeeksChanged(startDate)) },
        onCertifiedUsersClick = { viewModel.onIntent(FeedIntent.OnCertifiedUsersClick) },
        onPostUserProfileClick = { userId, isMe -> viewModel.onIntent(FeedIntent.OnPostUserProfileClick(userId, isMe)) },
        onPostMenuClick = { feedId -> viewModel.onIntent(FeedIntent.OnPostMenuClick(feedId)) },
        onPostMapImageClick = { feedId -> viewModel.onIntent(FeedIntent.OnPostMapImageClick(feedId)) },
        onPostReactionClick = { feedId, emoji -> viewModel.onIntent(FeedIntent.OnPostReactionClick(feedId, emoji)) },
        onPostReactionLongClick = { feedId, emojiType -> viewModel.onIntent(FeedIntent.OnPostReactionLongClick(feedId, emojiType)) },
        onPostAddReactionClick = { feedId -> viewModel.onIntent(FeedIntent.OnPostAddReactionClick(feedId)) },
        onBottomSheetDismiss = { viewModel.onIntent(FeedIntent.OnBottomSheetDismiss) },
        onBottomSheetUserProfileClick = { userId -> viewModel.onIntent(FeedIntent.OnBottomSheetUserProfileClick(userId)) },
        onEmojiSelected = { emoji -> viewModel.onIntent(FeedIntent.OnEmojiSelected(emoji)) }
    )
}
