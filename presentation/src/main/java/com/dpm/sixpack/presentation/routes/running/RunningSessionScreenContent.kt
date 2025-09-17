package com.dpm.sixpack.presentation.routes.running

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.running.component.MapConstants
import com.dpm.sixpack.presentation.routes.running.component.ReadyLoadingScreen
import com.dpm.sixpack.presentation.routes.running.contract.RunningSessionState
import com.dpm.sixpack.presentation.routes.running.contract.RunningSessionUiState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberFusedLocationSource

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionScreenContent(
    uiState: RunningSessionUiState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    onLocationChange: (LatLng) -> Unit,
    onStartClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties =
                MapProperties(
                    minZoom = MapConstants.MIN_ZOOM_LEVEL,
                    maxZoom = MapConstants.MAX_ZOOM_LEVEL,
                    locationTrackingMode = LocationTrackingMode.Follow,
                    isNightModeEnabled = isSystemInDarkTheme(),
                    mapType = MapType.Basic,
                ),
            uiSettings =
                MapUiSettings(
                    isZoomGesturesEnabled = true,
                    isZoomControlEnabled = false,
                    isLocationButtonEnabled = false,
                    isLogoClickEnabled = false,
                    isScaleBarEnabled = false,
                    isCompassEnabled = false,
                ),
            locationSource = locationSource,
            onLocationChange = { location ->
                onLocationChange(LatLng(location))
            },
        ) {
//            if (isSessionInProgress && uiState.path.size >= MIN_LENGTH_PATH_ARRAY) {
//                PathOverlay(
//                    coords = uiState.path,
//                    color = Color.Magenta,
//                    width = 5.dp,
//                )
//            }
        }

        when (uiState.sessionState) {
            RunningSessionState.Initial -> {
            }

            is RunningSessionState.MainReady -> {
            }

            is RunningSessionState.MainRunning -> {
            }

            is RunningSessionState.MainPause -> {
            }

            is RunningSessionState.CoolDownReady -> {
            }

            is RunningSessionState.Finished -> {
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 바텀 버튼
//            BottomLongTextButton(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(10.dp),
//                text = "러닝시작",
//                onClick = {
//                    showBottomSheet = true
//                    onStartClick()
//                },
//            )
        }

        if (uiState.sessionState is RunningSessionState.MainReady) {
            ReadyLoadingScreen(
                uiState.sessionState,
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Preview
@Composable
private fun PreviewRunningSessionScreenContent() {
    RunningSessionScreenContent(
        uiState =
            RunningSessionUiState(
                sessionState = RunningSessionState.MainReady(),
            ),
        cameraPositionState = CameraPositionState(),
        locationSource = rememberFusedLocationSource(),
        onLocationChange = { },
        onStartClick = { },
    )
}
