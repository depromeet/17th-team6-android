package com.dpm.sixpack.presentation.map.component

import android.Manifest

object MapConstants {
    const val DEFAULT_ZOOM = 16.0
    const val MIN_ZOOM_LEVEL = 6.0
    const val MAX_ZOOM_LEVEL = 19.0

    val MAP_PERMISSIONS =
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
}
