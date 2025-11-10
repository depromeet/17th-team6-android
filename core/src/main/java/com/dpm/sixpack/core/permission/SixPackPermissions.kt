package com.dpm.sixpack.core.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

private const val PREFS_NAME = "PermissionPrefs"
private const val KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested"
private const val KEY_BACKGROUND_LOCATION_PERMISSION_REQUESTED = "background_location_permission_requested"
private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
private const val KEY_FOREGROUND_SERVICE_PERMISSION_REQUESTED = "foreground_service_permission_requested"
private const val KEY_FOREGROUND_SERVICE_LOCATION_PERMISSION_REQUESTED =
    "foreground_service_location_permission_requested"
private const val KEY_ACTIVITY_RECOGNITION_PERMISSION_REQUESTED = "activity_recognition_permission_requested"
private const val KEY_IMAGE_PERMISSION_REQUESTED = "image_permission_requested"

sealed class SixPackPermissions(
    val permission: String,
    val prefKey: String,
) {
    object FineLocationPermission :
        SixPackPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            KEY_LOCATION_PERMISSION_REQUESTED,
        )

    object CourseLocationPermission :
        SixPackPermissions(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            KEY_LOCATION_PERMISSION_REQUESTED,
        )

    @RequiresApi(Build.VERSION_CODES.P)
    object ForegroundServicePermission :
        SixPackPermissions(
            Manifest.permission.FOREGROUND_SERVICE,
            KEY_FOREGROUND_SERVICE_PERMISSION_REQUESTED,
        )

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    object ForegroundServiceLocationPermission : SixPackPermissions(
        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
        KEY_FOREGROUND_SERVICE_LOCATION_PERMISSION_REQUESTED,
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    object ActivityRecognitionPermission :
        SixPackPermissions(
            Manifest.permission.ACTIVITY_RECOGNITION,
            KEY_ACTIVITY_RECOGNITION_PERMISSION_REQUESTED,
        )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object NotificationPermission :
        SixPackPermissions(
            Manifest.permission.POST_NOTIFICATIONS,
            KEY_NOTIFICATION_PERMISSION_REQUESTED,
        )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object ReadMediaImagesPermission : SixPackPermissions(
        Manifest.permission.READ_MEDIA_IMAGES,
        KEY_IMAGE_PERMISSION_REQUESTED,
    )

    object ReadExternalStoragePermission : SixPackPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        KEY_IMAGE_PERMISSION_REQUESTED,
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    object BackgroundLocationPermission : SixPackPermissions(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        KEY_BACKGROUND_LOCATION_PERMISSION_REQUESTED,
    )

    companion object {
        val LocationPermissions by lazy {
            listOf(
                FineLocationPermission,
                CourseLocationPermission,
            )
        }

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    add(ActivityRecognitionPermission)
                }
            }
        }

        val NotificationPermissions by lazy {
            mutableListOf<SixPackPermissions>().apply {
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
    }
}
