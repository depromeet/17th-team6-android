package com.dpm.sixpack.presentation.routes.running.map.contract

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
}
