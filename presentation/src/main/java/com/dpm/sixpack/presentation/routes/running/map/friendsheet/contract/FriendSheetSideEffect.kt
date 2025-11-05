package com.dpm.sixpack.presentation.routes.running.map.friendsheet.contract

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.routes.running.RunningRouteSideEffect

sealed interface FriendSheetSideEffect : RunningRouteSideEffect {
    data class UserItemClicked(
        val userId: Long,
    ) : FriendSheetSideEffect

    data class ShowToast(
        @StringRes val stringResId: Int,
        val args: String = "",
    ) : FriendSheetSideEffect
}
