package com.dpm.sixpack.presentation.routes.feed.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedBottomSheetState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalenderUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDateUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDialogState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.ReactionDetailsUiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedUiState(
    val selectedFeedId : Long = -1,
    val selectedPostMenuId : Long? = null,
    val calendarState: FeedCalenderUiState = FeedCalenderUiState(),
    val bottomSheetState: FeedBottomSheetState = FeedBottomSheetState(),
    val dialogState : FeedDialogState = FeedDialogState(),
    val reactionDetailsUiState: ReactionDetailsUiState = ReactionDetailsUiState.Loading,
    val postingUserInfo: List<PostingUserInfo> = listOf(),
    val optimisticPosts: Map<Long, PostResource> = emptyMap(),
    val feedDateState : FeedDateUiState = FeedDateUiState.NoPostsAndExpired
) : UiState,
    Parcelable
