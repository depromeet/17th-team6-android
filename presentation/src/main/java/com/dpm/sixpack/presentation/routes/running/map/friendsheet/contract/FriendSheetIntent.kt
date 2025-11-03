package com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract

import com.dpm.sixpack.presentation.routes.running.RunningRouteIntent

sealed interface FriendSheetIntent : RunningRouteIntent {
    data class ClickUser(
        val userId: Long,
    ) : FriendSheetIntent

    data class AwakeFriend(
        val userId: Long,
    ) : FriendSheetIntent
}
