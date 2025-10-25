package com.dpm.sixpack.presentation.common.model

import androidx.compose.runtime.Immutable

@Immutable
data class PostDetailUiState(
    val feedId: Int,
    val date: String,
    val userName: String,
    val userImageUrl: String,
    val postTime: String,
    val postImageUrl: String,
    val runningInfo: RunningSummaryUiState,
    val reactions: List<PostReactionUiState>
)
@Immutable
data class PostReactionUiState(
    val emojiType: String,
    val count: Int
)
