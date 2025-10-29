package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

// TODO date 는 사용하지 않는값으로 추후에 필요시 구현
@Parcelize
@Immutable
data class PostDetailUiState(
    val feedId: Int,
    val user: PostingUserState,
    val postImageUrl: String,
    val runningInfo: RunningSummaryUiState,
    val reactions: List<PostReactionState>,
) : Parcelable

@Parcelize

@Immutable
data class PostingUserState(
    val userName: String,
    val userImageUrl: String,
    val postingTime: String = "",
    val isMe: Boolean = false,
    val isMenuExpanded : Boolean = false
) : Parcelable

enum class PostDropDownActionType {
    EDIT,
    DELETE,
    SAVE_IMAGE,
    REPORT
}
