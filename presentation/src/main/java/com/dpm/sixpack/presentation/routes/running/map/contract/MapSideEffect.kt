package com.dpm.sixpack.presentation.routes.running.map.contract

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.routes.running.RunningRouteSideEffect
import com.naver.maps.geometry.LatLng

sealed interface MapSideEffect : RunningRouteSideEffect {
    data class SetCameraPosition(
        val latLng: LatLng,
    ) : MapSideEffect

    data class SetBottomBarVisibility(
        val isVisible: Boolean,
    ) : MapSideEffect

    data class NavigateToReport(
        val sessionId: Long,
    ) : MapSideEffect

    data class ShowToast(
        @StringRes val resId: Int,
        val args: List<Any>? = null,
    ) : MapSideEffect

    data class ShowToastWithMessage(
        val message: String,
    ) : MapSideEffect

    data object NavigateToFriendList : MapSideEffect
}
