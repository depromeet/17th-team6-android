package com.dpm.sixpack.presentation.routes.freind.contract

import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendUiState(
    val friendList: List<FriendUiItem> = emptyList(),
) : UiState
