package com.dpm.sixpack.presentation.routes.map.contract

import com.dpm.sixpack.presentation.util.base.SideEffect
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

    data class UpdateLocationPermission(
        val isGranted: Boolean,
    ) : MapSideEffect
}
