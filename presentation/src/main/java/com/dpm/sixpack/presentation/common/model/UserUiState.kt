package com.dpm.sixpack.presentation.common.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserUiState(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
    val isMe: Boolean = false,
)
