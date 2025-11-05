package com.dpm.sixpack.presentation.routes.running

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
    onShowSnackBar: (String, String?) -> Unit,
    navigateToReport: () -> Unit,
    setFullScreenLoading: (Boolean) -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Location
    val locationSource = rememberFusedLocationSource()

    // camera
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    RunningMapScreen(
        modifier = modifier.fillMaxSize(),
        onShowSnackBar = onShowSnackBar,
        locationSource = locationSource,
        cameraPositionState = cameraPositionState,
        onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        navigateToReport = navigateToReport,
        setFullScreenLoading = setFullScreenLoading,
    )
}
