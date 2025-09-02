package com.dpm.sixpack.presentation.map.component

import android.Manifest

object MapConstants {
    const val DEFAULT_ZOOM = 16.0

    val MAP_PERMISSIONS = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
}
