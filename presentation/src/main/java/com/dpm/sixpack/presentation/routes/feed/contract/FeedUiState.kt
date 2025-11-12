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
    // Post
    val feedDateState: FeedDateUiState = FeedDateUiState.PostsAvailable,
    val selectedPostMenuId: Long? = null,
    // Calender
    val calendarState: FeedCalenderUiState = FeedCalenderUiState(),
    val isCertifiableDate: Boolean = true,
    // Dialog
    val dialogState: FeedDialogState = FeedDialogState(),
    // BottomSheet
    val bottomSheetState: FeedBottomSheetState = FeedBottomSheetState(),
    val reactionDetailsUiState: ReactionDetailsUiState = ReactionDetailsUiState.Loading,
    // Certification User
    val postingUserInfo: List<PostingUserInfo> = listOf(),
    val myPostingInfo: PostingUserInfo? = null,
    val isMeCertified: Boolean = false,
    val isCertifiedUsersLoading: Boolean = false,
    // Optimistic Update
    val optimisticPosts: Map<Long, PostResource> = emptyMap(),
    val optimisticDeletedFeedIds: Set<Long> = emptySet(),
    val postForEmojiSelection: PostResource? = null,
) : UiState,
    Parcelable
