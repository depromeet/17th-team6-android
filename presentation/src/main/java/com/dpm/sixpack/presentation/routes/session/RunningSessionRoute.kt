package com.dpm.sixpack.presentation.routes.session

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.map.component.MapConstants
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionRoute(
    modifier: Modifier = Modifier,
    viewModel: RunningSessionViewModel = hiltViewModel(),
) {
    val uiState = viewModel.collectAsState()
    viewModel.collectSideEffect { }

    // Location
    val locationSource = rememberFusedLocationSource()

    // State
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }


    RunningSessionScreen(
        runningSessionUiState = uiState.value,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onLocationChange = { latLng ->
        },
        onStartClick = {
        }
    )
}
