package com.dpm.sixpack.presentation.common.model

import androidx.compose.runtime.Immutable

//TODO date 는 사용하지 않는값으로 추후에 필요시 구현
@Immutable
data class PostDetailUiState(
    val feedId: Int,
    val user: PostingUserUiState,
    val postImageUrl: String,
    val runningInfo: RunningSummaryUiState,
    val reactions: List<PostReactionUiState>
)

@Immutable
data class PostingUserUiState(
    val userName: String,
    val userImageUrl: String,
    val postingTime: String = "",
    val isMe : Boolean = false
)
