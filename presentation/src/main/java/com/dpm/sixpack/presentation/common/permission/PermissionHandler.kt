package com.dpm.sixpack.presentation.common.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * General permission handler
 *
 * @param permissionsToRequest List of permissions to request (예: arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
 * @param onPermissionResult A callback function that is called when the permission request is completed
 */
@Composable
fun PermissionHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    permissionsToRequest: List<String>,
    onPermissionResult: (Boolean) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val isGranted = permissions.values.all { it }
            onPermissionResult(isGranted)
        }

    // 화면이 다시 활성화될 때마다 권한을 체크
    DisposableEffect(lifecycleOwner, permissionsToRequest) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val allPermissionsGranted =
                        permissionsToRequest.all {
                            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                        }
                    if (allPermissionsGranted) {
                        onPermissionResult(true)
                    }
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 최초 진입 시 권한을 요청
    LaunchedEffect(permissionsToRequest) {
        val allPermissionsGranted = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allPermissionsGranted) {
            launcher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
