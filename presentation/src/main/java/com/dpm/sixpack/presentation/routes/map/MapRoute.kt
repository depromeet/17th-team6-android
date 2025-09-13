package com.dpm.sixpack.presentation.routes.map

import android.os.Debug
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.presentation.common.permission.PermissionHandler
import com.dpm.sixpack.presentation.routes.map.component.MapConstants
import com.dpm.sixpack.presentation.routes.map.contract.MapIntent
import com.dpm.sixpack.presentation.routes.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.util.COORDS_1
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
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onLocationChange = { latLng ->
            viewModel.onIntent(MapIntent.UpdateUserLocation(latLng))
        },
        onFabClick = {
            if (isDebugMode) {
                if (uiState.isMockSimulating) {
                    viewModel.onIntent(MapIntent.StopMockSimulation)
                } else {
                    viewModel.onIntent(MapIntent.StartMockSimulation(COORDS_1))
                }
            } else {
                val newRunningMode = !uiState.runningMode
                viewModel.onIntent(MapIntent.ChangeRunningMode(newRunningMode))
            }
        },
    )
}
