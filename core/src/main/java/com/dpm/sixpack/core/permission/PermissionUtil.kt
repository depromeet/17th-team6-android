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

    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun savePermissionRequested(
        context: Context,
        permission: SixPackPermissions,
    ) {
        val key = permission.prefKey
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
            getPreferences(context).getBoolean(permission.prefKey, false)
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

    fun requestPermissions(
        context: Context,
        permissionsLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
        permissions: List<SixPackPermissions>,
    ) {
        permissions.forEach {
            savePermissionRequested(context, it)
        }
        permissionsLauncher.launch(permissions.toStringArray())
    }

    fun requestPermission(
        context: Context,
        permissionsLauncher: ManagedActivityResultLauncher<String, Boolean>,
        permissions: SixPackPermissions,
    ) {
        savePermissionRequested(context, permissions)
        permissionsLauncher.launch(permissions.permission)
    }

    fun clearAllPermissionData(context: Context) {
        getPreferences(context).edit { clear() }
    }

    private fun List<SixPackPermissions>.toStringArray(): Array<String> = map { it.permission }.toTypedArray()
}
