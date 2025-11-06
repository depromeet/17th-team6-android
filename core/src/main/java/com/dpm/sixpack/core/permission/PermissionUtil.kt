package com.dpm.sixpack.core.permission

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import timber.log.Timber

/*
 * 권한 관련 util, 모든 권한 요청은 여기서 처리되어야함.
 */
object PermissionUtil {
    private const val PREFS_NAME = "PermissionPrefs"
    private const val KEY_LOCATION_PERMISSION_REQUESTED = "location_permission_requested"
    private const val KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"

    private const val KEY_IMAGE_PERMISSION_REQUESTED = "image_permission_requested"

    private fun mapPermissionToKey(permission: SixPackPermissions): String =
        when (permission) {
            is SixPackPermissions.FineLocationPermission -> KEY_LOCATION_PERMISSION_REQUESTED
            is SixPackPermissions.CourseLocationPermission -> KEY_LOCATION_PERMISSION_REQUESTED
            is SixPackPermissions.NotificationPermission -> KEY_NOTIFICATION_PERMISSION_REQUESTED
            is SixPackPermissions.ReadMediaImagesPermission -> KEY_IMAGE_PERMISSION_REQUESTED
            is SixPackPermissions.ReadExternalStoragePermission -> KEY_IMAGE_PERMISSION_REQUESTED
            else -> throw IllegalArgumentException("Invalid permission: $permission")
        }

    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun savePermissionRequested(
        context: Context,
        permission: SixPackPermissions,
    ) {
        val key = mapPermissionToKey(permission)
        Timber.d("king : $key")
        getPreferences(context).edit { putBoolean(key, true) }
    }

    private fun isPermissionRequested(
        context: Context,
        permission: SixPackPermissions,
    ): Boolean =
        if (permission is SixPackPermissions.NotificationPermission &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            savePermissionRequested(context, permission)
            true
        } else {
            getPreferences(context).getBoolean(mapPermissionToKey(permission), false)
        }

    fun hasPermissions(
        context: Context,
        permissions: List<SixPackPermissions>,
    ): Boolean =
        if (permissions.isEmpty()) {
            true
        } else {
            permissions.toStringArray().all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }

    fun requestPermission(
        context: Context,
        permissionsLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
        permissions: List<SixPackPermissions>,
    ) {
        permissions.forEach {
            savePermissionRequested(context, it)
        }
        permissionsLauncher.launch(permissions.toStringArray())
    }

    fun clearAllPermissionData(context: Context) {
        getPreferences(context).edit { clear() }
    }

    private fun List<SixPackPermissions>.toStringArray(): Array<String> = map { it.permission }.toTypedArray()
}
