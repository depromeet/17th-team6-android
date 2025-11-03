package com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract

import com.dpm.sixpack.presentation.routes.running.RunningRouteSideEffect

sealed interface FriendSheetSideEffect : RunningRouteSideEffect {
    data class UserItemClicked(
        val userId: Long,
    ) : FriendSheetSideEffect
}
