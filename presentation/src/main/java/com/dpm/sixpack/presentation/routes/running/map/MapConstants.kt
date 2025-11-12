package com.dpm.sixpack.presentation.routes.running.map

import com.dpm.sixpack.core.permission.SixPackPermissions.Companion.ForegroundServicePermissions
import com.dpm.sixpack.core.permission.SixPackPermissions.Companion.LocationPermissions
import com.dpm.sixpack.core.permission.SixPackPermissions.Companion.NotificationPermissions
import com.dpm.sixpack.core.permission.SixPackPermissions.Companion.SensorPermissions
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition

object MapConstants {
    const val DEFAULT_ZOOM = 16.0
    const val MIN_ZOOM_LEVEL = 6.0
    const val MAX_ZOOM_LEVEL = 18.0
    const val SNAPSHOT_PADDING = 300
    const val FINAL_RESOLUTION = 720

    val DEFAULT_CAMERA_POSITION =
        CameraPosition(
            // 서울 시
            LatLng(37.565239, 126.977347),
            DEFAULT_ZOOM,
        )

    val MAP_PERMISSIONS =
        LocationPermissions + SensorPermissions + NotificationPermissions + ForegroundServicePermissions
}
