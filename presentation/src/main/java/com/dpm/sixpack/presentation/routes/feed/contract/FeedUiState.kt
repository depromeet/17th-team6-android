package com.dpm.sixpack.presentation.routes.feed.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedBottomSheetState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalenderUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.ReactionDetailsUiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedUiState(
    val calendarState: FeedCalenderUiState = FeedCalenderUiState(),
    val bottomSheetState: FeedBottomSheetState = FeedBottomSheetState(),
    val postCardsState: List<PostResource> = listOf(),
    val postingUserInfo: List<PostingUserInfo> = listOf(),
    val menuExpandedFeedId: Int? = null
) : UiState,
    Parcelable
