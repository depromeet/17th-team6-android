package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.session.component.MapConstants
import com.dpm.sixpack.presentation.routes.session.component.ready.ReadyOverlay
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionUiState
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
import timber.log.Timber

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionScreenContent(
    uiState: RunningSessionUiState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    onLocationChange: (LatLng) -> Unit,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
                    isScrollGesturesEnabled = false,
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
                InitialContent(onStartClick = {
                    onStartClick()
                })
            }

            is RunningSessionState.ReadyState -> {
                ReadyOverlay(
                    modifier = Modifier.fillMaxSize(),
                    readyState = uiState.sessionState,
                )
            }

            is RunningSessionState.WarmUp.Running -> {}
            is RunningSessionState.WarmUp.Pause -> {}
            is RunningSessionState.Main.Running -> {}
            is RunningSessionState.Main.Pause -> {}
            is RunningSessionState.CoolDown.Pause -> TODO()
            is RunningSessionState.CoolDown.Running -> TODO()
        }
    }
}

@Composable
private fun InitialContent(onStartClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        DoRunDefaultButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            onClick = {
                Timber.d("Running Session Start Clicked")
                onStartClick()
            },
            text = stringResource(id = R.string.session_start_running_button),
        )
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Preview
@Composable
private fun PreviewRunningSessionScreenContent() {
    RunningSessionScreenContent(
        uiState =
            RunningSessionUiState(
                sessionState = RunningSessionState.WarmUp.Ready(),
            ),
        cameraPositionState = CameraPositionState(),
        locationSource = rememberFusedLocationSource(),
        onLocationChange = { },
        onStartClick = { },
    )
}
