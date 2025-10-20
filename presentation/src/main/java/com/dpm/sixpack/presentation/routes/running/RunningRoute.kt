package com.dpm.sixpack.presentation.routes.running

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dpm.sixpack.presentation.routes.running.map.MapConstants
import com.dpm.sixpack.presentation.routes.running.map.RunningMapScreen
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningRoute(
    onNavigateToBack: () -> Unit,
    navigateToReport: () -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {
    // Location
    val locationSource = rememberFusedLocationSource()

    // camera
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    Box(
        modifier =
            Modifier.fillMaxSize(),
    ) {
//        RunningSessionScreen(
//            modifier = modifier,
//            uiState = uiState,
//            cameraPositionState = cameraPositionState,
//            locationSource = locationSource,
//            onLocationChange = { latLng ->
//                cameraPositionState.move(CameraUpdate.scrollTo(latLng))
//            },
//            onIntent = viewModel::onIntent,
//        )
//
//        if (uiState is RunningSessionUiState.Initial) {
//            InitialContent(
//                onStartClick = {
//                    viewModel.onIntent(RunningSessionIntent.SessionStart)
//                },
//            )
//        }
        RunningMapScreen(
            modifier = Modifier.fillMaxSize(),
            locationSource = locationSource,
            cameraPositionState = cameraPositionState,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            navigateToReport = navigateToReport,
        )
    }
}
