package com.dpm.sixpack.presentation.routes.postdetail.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedBottomSheetState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDialogState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.ReactionDetailsUiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val feedId: Long = -1,
    val post: PostResource? = null,
    val isMenuExpanded: Boolean = false,
    val dialogState: FeedDialogState = FeedDialogState(),
    val bottomSheetState: FeedBottomSheetState = FeedBottomSheetState(),
    val reactionDetailsUiState: ReactionDetailsUiState = ReactionDetailsUiState.Loading,
    val postForEmojiSelection: PostResource? = null,
) : UiState,
    Parcelable
