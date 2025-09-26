package com.dpm.sixpack.presentation.routes.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.presentation.common.util.PermissionHandler
import com.dpm.sixpack.presentation.routes.session.component.MapConstants
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionSubRoute(
    viewModel: RunningSessionViewModel,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    modifier: Modifier = Modifier,
) {
    // state
    val uiState by viewModel.collectAsState()

    PermissionHandler(
        context = LocalContext.current,
        lifecycleOwner = LocalLifecycleOwner.current,
        permissionsToRequest = MapConstants.MAP_PERMISSIONS,
        onPermissionResult = { isGranted ->
            viewModel.onIntent(RunningSessionIntent.UpdatePermission(isGranted))
        },
    )

    RunningSessionScreen(
        modifier = modifier,
        uiState = uiState,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onLocationChange = { latLng ->
            cameraPositionState.move(CameraUpdate.scrollTo(latLng))
        },
        onTrackingButtonClick = { trackingIntent ->
            viewModel.onIntent(trackingIntent)
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
