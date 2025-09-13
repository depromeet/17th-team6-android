package com.dpm.sixpack.presentation.routes.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.map.component.MapConstants
import com.dpm.sixpack.presentation.routes.map.component.MapConstants.MIN_LENGTH_PATH_ARRAY
import com.dpm.sixpack.presentation.routes.map.contract.MapState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    uiState: MapState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    onLocationChange: (LatLng) -> Unit,
    onFabClick: () -> Unit,
) {
    val isSessionInProgress = uiState.runningMode || uiState.isMockSimulating
    val runningState = uiState.runningState

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
                    isLocationButtonEnabled = true,
                    isLogoClickEnabled = false,
                    isScaleBarEnabled = false,
                    isCompassEnabled = false,
                ),
            locationSource = locationSource,
            onLocationChange = { location ->
                onLocationChange(LatLng(location))
            },
        ) {
            if (isSessionInProgress && uiState.path.size >= MIN_LENGTH_PATH_ARRAY) {
                PathOverlay(
                    coords = uiState.path,
                    color = Color.Magenta,
                    width = 5.dp,
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(color = Color.Black, text = "시간: ${runningState.duration / 1000}초")
            Text(color = Color.Black, text = "거리: ${runningState.distance.toInt()}m")
            Text(color = Color.Black, text = "페이스: ${runningState.paceInMoment}")
            Text(color = Color.Black, text = "케이던스: ${runningState.cadence} SPM")
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FloatingActionButton(
                    containerColor = Color(0xFFABABE7),
                    onClick = {
                        onFabClick()
                    },
                ) {
                    Icon(
                        imageVector =
                            if (isSessionInProgress) {
                                Icons.Default.Stop
                            } else {
                                Icons.Default.PlayArrow
                            },
                        contentDescription = "Start Running Button",
                        tint = Color.Black,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Preview
@Composable
private fun MapScreenPreview() {
    MapScreen(
        uiState = MapState(),
        cameraPositionState = rememberCameraPositionState(),
        locationSource = rememberFusedLocationSource(),
        onLocationChange = {
        },
        onFabClick = {
        },
    )
}
