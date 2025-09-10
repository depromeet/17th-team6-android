package com.dpm.sixpack.presentation.common.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

sealed class SixPackPermissions(val permission: String) {
    class FineLocationPermission : SixPackPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
    class CourseLocationPermission : SixPackPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    class NotificationPermission : SixPackPermissions(Manifest.permission.POST_NOTIFICATIONS)

    companion object {
        val LocationPermissions = listOf(
            FineLocationPermission(),
            CourseLocationPermission()
        )

        val RequiredPermissions = mutableListOf<SixPackPermissions>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(NotificationPermission())
            }
        }

        val OptionalPermissions = LocationPermissions + listOf()

        val AllPermissions = RequiredPermissions + OptionalPermissions
    }
}
