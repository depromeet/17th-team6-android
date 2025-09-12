package com.dpm.sixpack.presentation.map.component

import com.dpm.sixpack.core.permission.SixPackPermissions
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition

object MapConstants {
    const val DEFAULT_ZOOM = 16.0
    const val MIN_ZOOM_LEVEL = 6.0
    const val MAX_ZOOM_LEVEL = 19.0
    const val MIN_LENGTH_PATH_ARRAY = 2
    const val MIN_DISTANCE_BETWEEN_PATH = 5.0
    val DEFAULT_CAMERA_POSITION =
        CameraPosition(
            LatLng(37.5665, 126.9780),
            DEFAULT_ZOOM,
        )

    val MAP_PERMISSIONS = SixPackPermissions.LocationPermissions
}
