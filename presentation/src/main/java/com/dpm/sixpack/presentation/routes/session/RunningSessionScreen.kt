package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.session.component.MapConstants
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionScreen(
    onSessionStart: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RunningSessionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.collectAsState()
    viewModel.collectSideEffect { }

    // Location
    val locationSource = rememberFusedLocationSource()

    // State
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    RunningSessionScreenContent(
        modifier = modifier,
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onLocationChange = { },
        onStartClick = { },
        onSessionStart = onSessionStart,
    )
}

@Composable
fun TestScreen() {
    Box(modifier = Modifier.fillMaxSize())
}
