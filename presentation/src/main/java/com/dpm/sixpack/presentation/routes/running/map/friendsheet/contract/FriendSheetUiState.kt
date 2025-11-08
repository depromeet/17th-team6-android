package com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract

import com.dpm.sixpack.presentation.routes.running.RunningRouteUiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendSheetUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) : RunningRouteUiState
