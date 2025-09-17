package com.dpm.sixpack.presentation.common.util

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.dpm.sixpack.core.permission.PermissionUtil
import com.dpm.sixpack.core.permission.SixPackPermissions

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
    permissionsToRequest: List<SixPackPermissions>,
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
                    val allPermissionsGranted = PermissionUtil.hasPermissions(context, permissionsToRequest)

                    if (allPermissionsGranted) {
                        onPermissionResult(true)
                    } else {
                        onPermissionResult(false)
                        // TODO: 권한이 없는 경우, 다시 요청 혹은 요청 기록이 있으면 설정 연결
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
        val allPermissionsGranted =
            PermissionUtil.hasPermissions(context, permissionsToRequest)

        if (!allPermissionsGranted) {
            PermissionUtil.requestPermission(context, launcher, permissionsToRequest)
        }
    }
}
