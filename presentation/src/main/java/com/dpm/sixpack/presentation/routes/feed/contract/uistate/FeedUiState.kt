package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.PostDetailUiState
import com.dpm.sixpack.presentation.common.model.PostingUserState
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedUiState(
    val calendarState: FeedCalenderUiState = FeedCalenderUiState(),
    val bottomSheetState: FeedBottomSheetState = FeedBottomSheetState(),
    val postCardsState: List<PostDetailUiState> = listOf(),
    val postingUserState: List<PostingUserState> = listOf(),
    val reactionDetailsUiState: ReactionDetailsUiState = ReactionDetailsUiState(),
    val menuExpandedFeedId: Int? = null
) : UiState,
    Parcelable

