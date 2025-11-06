package com.dpm.sixpack.core.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

sealed class SixPackPermissions(
    val permission: String,
) {
    object FineLocationPermission : SixPackPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

    object CourseLocationPermission : SixPackPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.P)
    object ForegroundServicePermission : SixPackPermissions(Manifest.permission.FOREGROUND_SERVICE)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    object ForegroundServiceLocationPermission : SixPackPermissions(Manifest.permission.FOREGROUND_SERVICE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.Q)
    object ActivityRecognitionPermission : SixPackPermissions(Manifest.permission.ACTIVITY_RECOGNITION)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object NotificationPermission : SixPackPermissions(Manifest.permission.POST_NOTIFICATIONS)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object ReadMediaImagesPermission : SixPackPermissions(Manifest.permission.READ_MEDIA_IMAGES)

    object ReadExternalStoragePermission : SixPackPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

    companion object {
        val LocationPermissions =
            listOf(
                FineLocationPermission,
                CourseLocationPermission,
            )

        val ForegroundServicePermissions by lazy {
            mutableListOf<SixPackPermissions>().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ForegroundServicePermission)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    add(ForegroundServiceLocationPermission)
                }
            }
        }

        val SensorPermissions by lazy {
            mutableListOf<SixPackPermissions>().apply {
                addAll(ForegroundServicePermissions)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(ActivityRecognitionPermission)
                }
            }
        }

        val RequiredPermissions by lazy {
            mutableListOf<SixPackPermissions>().apply {
                addAll(SensorPermissions)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(NotificationPermission)
                }
            }
        }

        val ImagePermissions by lazy {
            mutableListOf<SixPackPermissions>().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(ReadMediaImagesPermission)
                } else {
                    add(ReadExternalStoragePermission)
                }
            }
        }

        val OptionalPermissions = LocationPermissions

        val AllPermissions = RequiredPermissions + OptionalPermissions
    }
}
