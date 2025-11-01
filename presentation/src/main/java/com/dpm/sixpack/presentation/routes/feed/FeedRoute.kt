package com.dpm.sixpack.presentation.routes.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.routes.feed.contract.FeedIntent
import com.dpm.sixpack.presentation.routes.feed.contract.FeedSideEffect
import com.dpm.sixpack.presentation.routes.feed.ui.screen.FeedScreen
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
            is FeedSideEffect.ShowToast -> {
                // TODO: Show toast
            }
            is FeedSideEffect.RefreshPagingList -> feedPagingItems.refresh()
        }
    }

    FeedScreen(
        state = state,
        feedPagingItems = feedPagingItems,
        onTopBarGroupIconClick = { viewModel.onIntent(FeedIntent.OnTopBarGroupIconClick) },
        onTopBarAlarmIconClick = { viewModel.onIntent(FeedIntent.OnTopBarAlarmIconClick) },
        // Calendar
        onDateSelected = { date -> viewModel.onIntent(FeedIntent.OnDateSelected(date)) },
        onVisibleWeeksChanged = { startDate -> viewModel.onIntent(FeedIntent.OnVisibleWeeksChanged(startDate)) },
        // Certified Users
        onCertifiedUsersClick = { viewModel.onIntent(FeedIntent.OnCertifiedUsersClick) },

        // FeedPostCard의 시그니처 변경이 필요합니다. (아래 7번 항목 참고)
        onPostUserProfileClick = { userId, isMe -> viewModel.onIntent(FeedIntent.OnUserProfileClick(userId, isMe)) },
        onPostMenuClick = { feedId -> viewModel.onIntent(FeedIntent.OnPostMenuClick(feedId)) },
        onPostImageClick = { post -> viewModel.onIntent(FeedIntent.OnPostImageClick(post)) },
        onPostReactionClick = { post, emoji, isReacted -> viewModel.onIntent(FeedIntent.OnPostReactionClick(post, emoji, isReacted)) },
        onPostReactionLongClick = { feedId, reactions, emoji -> viewModel.onIntent(FeedIntent.OnPostReactionLongClick(feedId, reactions, emoji)) },
        onPostAddReactionClick = { post -> viewModel.onIntent(FeedIntent.OnPostAddReactionClick(post)) },
        onDropDownMenuClick = { feedId, action -> viewModel.onIntent(FeedIntent.OnDropDownMenuClick(feedId, action)) },

        // BottomSheet (결정 5, 7 반영)
        onBottomSheetDismiss = { viewModel.onIntent(FeedIntent.OnBottomSheetDismiss) },
        onUserReactionSheetUserProfileClick = { userId, isMe -> viewModel.onIntent(FeedIntent.OnUserProfileClick(userId, isMe)) },
        onUserReactionSheetTabClick = { emoji -> viewModel.onIntent(FeedIntent.OnUserReactionSheetTabClick(emoji)) },
        onEmojiSelected = { emoji -> viewModel.onIntent(FeedIntent.OnEmojiSheetEmojiSelected(emoji)) },

        // Dialog
        onDialogDismiss = { viewModel.onIntent(FeedIntent.OnDialogDismiss) },
        onDialogConfirmClick = { viewModel.onIntent(FeedIntent.OnDialogConfirmClick) },

        // FAB
        onFTAButtonClick = { viewModel.onIntent(FeedIntent.OnFloatingActionButtonClick) }
    )}
