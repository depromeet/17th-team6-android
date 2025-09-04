package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.SideEffect
import com.naver.maps.geometry.LatLng

sealed interface MapSideEffect : SideEffect {
    data class MoveCameraToPosition(
        val latLng: LatLng,
    ) : MapSideEffect

    data class ShowToast(
        val messageResId: Int,
    ) : MapSideEffect

    data object SetInitialLocation : MapSideEffect
}
