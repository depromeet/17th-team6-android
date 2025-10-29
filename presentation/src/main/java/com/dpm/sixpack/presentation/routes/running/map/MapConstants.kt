package com.dpm.sixpack.presentation.routes.running.map

import com.dpm.sixpack.core.permission.SixPackPermissions
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition

object MapConstants {
    const val DEFAULT_ZOOM = 16.0
    const val MIN_ZOOM_LEVEL = 6.0
    const val MAX_ZOOM_LEVEL = 18.0

    val DEFAULT_CAMERA_POSITION =
        CameraPosition(
            // 성수
            LatLng(37.546914, 127.066506),
            DEFAULT_ZOOM,
        )

    val MAP_PERMISSIONS = SixPackPermissions.Companion.LocationPermissions
}
