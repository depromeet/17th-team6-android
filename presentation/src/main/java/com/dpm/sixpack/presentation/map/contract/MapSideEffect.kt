package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.SideEffect
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition

sealed interface MapSideEffect : SideEffect {
    data class ScrollCameraPosition(
        val latLng: LatLng,
    ) : MapSideEffect

    data class ChangeCameraPosition(
        val cameraPosition: CameraPosition,
    ) : MapSideEffect

    data class ShowToast(
        val messageResId: Int,
    ) : MapSideEffect

    data class SetInitialLocation(
        val isGranted: Boolean,
    ) : MapSideEffect
}
