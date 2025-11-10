package com.dpm.sixpack.presentation.common.util

import android.content.Context
import android.os.Build
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
 * 백그라운드 위치 권한("항상 허용")을 전담으로 처리하는 핸들러.
 * 안드로이드 11+의 2단계 권한 요청 로직을 자동으로 수행합니다.
 *
 * @param onPermissionResult "항상 허용"이 최종 승인되었는지 여부를 반환 (true/false)
 * @param onShowRationale "항상 허용"이 왜 필요한지 설명하는 다이얼로그를 띄워야 할 때 호출됩니다.
 * 이 콜백은 "설정으로 이동"하는 람다(launcher)를 파라미터로 전달합니다.
 * UI는 이 람다를 "확인" 버튼 등에 연결해야 합니다.
 */
@Composable
fun BackgroundPermissionHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    foregroundPermissions: List<SixPackPermissions>,
    backgroundPermission: SixPackPermissions? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) SixPackPermissions.BackgroundLocationPermission else null,
    onPermissionResult: (Boolean, Boolean) -> Unit,
    onShowRationale: (launchSettings: () -> Unit) -> Unit,
) {
    // --- 필요한 권한 문자열 정의 ---
    fun hasForegroundPermission(): Boolean = PermissionUtil.hasPermissions(context, foregroundPermissions)

    fun hasBackgroundPermission(): Boolean =
        if (backgroundPermission != null) PermissionUtil.hasPermissions(context, listOf(backgroundPermission)) else true

    val backgroundLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) {
            // 설정 화면에서 돌아왔을 때, "항상 허용"이 되었는지 최종 체크
            onPermissionResult(hasForegroundPermission(), hasBackgroundPermission())
        }

    // --- 전경 권한 런처 (앱 내 팝업) ---
    val foregroundLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val isForegroundGranted = permissions.values.any { it }

            if (isForegroundGranted) {
                // 1단계(전경) 성공 -> 2단계(백그라운드)를 위해 Rationale(이유) UI를 띄우라고 요청
                backgroundPermission?.let {
                    onShowRationale {
                        PermissionUtil.requestPermission(context, backgroundLauncher, backgroundPermission)
                    }
                }
            } else {
                // 1단계(전경) 실패 -> 최종 실패
                onPermissionResult(false, false)
            }
        }

    // --- 3. 화면이 ON_RESUME 될 때마다 권한 상태를 다시 체크 ---
    // (사용자가 설정에서 수동으로 권한을 켜고 돌아오는 경우)
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    // 현재 "항상 허용" 상태인지 체크해서 알려줌
                    onPermissionResult(hasForegroundPermission(), hasBackgroundPermission())
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // --- 4. 컴포저블이 처음 실행될 때 권한 요청 로직 (1회) ---
    LaunchedEffect(Unit) {
        if (hasBackgroundPermission()) {
            // 이미 "항상 허용" 상태면, 즉시 성공 반환
            onPermissionResult(true, true)
            return@LaunchedEffect
        }

        if (!hasForegroundPermission()) {
            // 1단계(전경) 권한이 없으므로, 전경 권한부터 요청
            PermissionUtil.requestPermissions(context, foregroundLauncher, foregroundPermissions)
            return@LaunchedEffect
        }

        // 1단계(전경)는 있지만 2단계(백그라운드)가 없는 경우
        // Rationale(이유) UI를 띄우라고 즉시 요청
        backgroundPermission?.let {
            onShowRationale {
                PermissionUtil.requestPermission(context, backgroundLauncher, backgroundPermission)
            }
        }
    }
}
