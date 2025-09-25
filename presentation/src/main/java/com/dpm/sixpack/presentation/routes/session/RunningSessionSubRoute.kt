package com.dpm.sixpack.presentation.routes.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dpm.sixpack.presentation.routes.session.component.MapConstants
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionSubRoute(
    viewModel: RunningSessionViewModel,
    modifier: Modifier = Modifier,
) {
    // state
    val uiState by viewModel.collectAsState()

    // Location
    val locationSource = rememberFusedLocationSource()

    // State
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    RunningSessionScreen(
        modifier = modifier,
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onLocationChange = {
            // todo
        },
        onPauseClick = { pauseIntent ->
            viewModel.onIntent(pauseIntent)
        },
        onResumeClick = { resumeIntent ->
            viewModel.onIntent(resumeIntent)
        },
        onStopClick = { stopIntent ->
            viewModel.onIntent(stopIntent)
        },
        onStopCancelClick = { cancelIntent ->
            viewModel.onIntent(cancelIntent)
        },
        onStopConfirmClick = { stopConfirmIntent ->
            viewModel.onIntent(stopConfirmIntent)
        },
        onSkipClick = {
            viewModel.onIntent(RunningSessionIntent.WarmUpSkip)
        },
        onSkipCancelClick = {
            viewModel.onIntent(RunningSessionIntent.WarmUpSkipCancel)
        },
        onSkipConfirmClick = {
            viewModel.onIntent(RunningSessionIntent.WarmUpSkipConfirm)
        },
    )
}
