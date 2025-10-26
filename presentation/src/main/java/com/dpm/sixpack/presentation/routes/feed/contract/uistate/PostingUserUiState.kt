package com.dpm.sixpack.presentation.routes.feed.contract.uistate

import androidx.compose.runtime.Immutable


@Immutable
data class PostingUserUiState(
    val userName: String,
    val userImageUrl: String,
    val currentPostTime: String = "",
    val isMe : Boolean = false
)
