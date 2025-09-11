package com.dpm.sixpack.presentation.map

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Debug
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dpm.sixpack.presentation.common.permission.PermissionHandler
import com.dpm.sixpack.presentation.map.component.MapConstants
import com.dpm.sixpack.presentation.map.contract.MapIntent
import com.dpm.sixpack.presentation.map.contract.MapSideEffect
import com.dpm.sixpack.runningservice.RunningService
import com.dpm.sixpack.runningservice.RunningState
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapRoute(viewModel: MapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // RunningService
    var runningService by remember { mutableStateOf<RunningService?>(null) }
    val runningDataState by runningService?.runningDataState?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(RunningState()) }
    val runningTimeState by runningService?.runningTimeState?.collectAsStateWithLifecycle()
        ?: remember { mutableLongStateOf(0L) }

    // Location
    val locationSource = rememberFusedLocationSource()

    // State
    val uiState by viewModel.collectAsState()
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    // DEBUG - MOCK LOCATION, REMOVE WHEN RELEASE
    // FIXME: SK
    val isDebugMode = remember { Debug.isDebuggerConnected() }

    DisposableEffect(Unit) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                runningService = (service as RunningService.RunningBinder).getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                runningService = null
            }
        }

        Intent(context, RunningService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        onDispose {
            context.unbindService(connection)
        }
    }

    viewModel.collectSideEffect { mapSideEffect ->
        when (mapSideEffect) {
            is MapSideEffect.ShowToast -> {
                // TODO
            }

            is MapSideEffect.ScrollCameraPosition -> {
                cameraPositionState.move(CameraUpdate.scrollTo(mapSideEffect.latLng))
            }

            is MapSideEffect.ChangeCameraPosition -> {
                cameraPositionState.position = mapSideEffect.cameraPosition
            }

            is MapSideEffect.UpdateLocationPermission -> {
                if (!uiState.isInitialLocationSet && mapSideEffect.isGranted) {
                    viewModel.onIntent(MapIntent.SetInitialLocation)
                } else {
                    viewModel.onIntent(MapIntent.ChangeCameraPosition(MapConstants.DEFAULT_CAMERA_POSITION))
                    // TODO SK: 권한 비허용 안내 및 권한 허용 유도
                }
            }
        }
    }

    PermissionHandler(
        context = context,
        lifecycleOwner = lifecycleOwner,
        permissionsToRequest = MapConstants.MAP_PERMISSIONS,
        onPermissionResult = { isGranted ->
            viewModel.onIntent(MapIntent.UpdateLocationPermission(isGranted))
        },
    )

    MapScreen(
        uiState = uiState,
        runningDataState = runningDataState,
        runningTimeState = runningTimeState,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onLocationChange = { latLng ->
            viewModel.onIntent(MapIntent.UpdateUserLocation(latLng))
        },
        onFabClick = {
            if (isDebugMode) {
                if (uiState.isMockSimulating) {
                    viewModel.onIntent(MapIntent.StopMockSimulation)

                    // Service Stop
                    stopRunningService(context)
                } else {
                    viewModel.onIntent(MapIntent.StartMockSimulation(COORDS_1))

                    // Service Start
                    startRunningService(context)
                }
            } else {
                val newRunningMode = !uiState.runningMode
                viewModel.onIntent(MapIntent.ChangeRunningMode(newRunningMode))

                if (newRunningMode) startRunningService(context)
            }
        },
    )
}

private fun startRunningService(context: Context) {
    val intent = Intent(context, RunningService::class.java).apply {
        action = RunningService.ACTION_START_OR_RESUME_SERVICE
    }
    context.startService(intent)
}

private fun stopRunningService(context: Context) {
    val intent = Intent(context, RunningService::class.java).apply {
        action = RunningService.ACTION_STOP_SERVICE
    }
    context.startService(intent)
}
