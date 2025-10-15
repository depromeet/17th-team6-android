package com.dpm.sixpack.presentation.routes.session

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.util.PermissionHandler
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionSideEffect
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningSessionState
import com.dpm.sixpack.presentation.routes.session.ui.RunningSessionScreen
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionRoute(
    modifier: Modifier = Modifier,
    onNavigateToBack: () -> Unit = { },
    navigateToSessionReport: () -> Unit = { },
    viewModel: RunningSessionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.collectAsState()

    // Location
    val locationSource = rememberFusedLocationSource()

    // camera
    val cameraPositionState =
        rememberCameraPositionState {
            position = MapConstants.DEFAULT_CAMERA_POSITION
        }

    viewModel.collectSideEffect { sideEffect ->
        // Collect
        when (sideEffect) {
            is RunningSessionSideEffect.NavigateBackToHome -> {
                onNavigateToBack()
            }

            is RunningSessionSideEffect.NavigateToReport -> {
                navigateToSessionReport()
            }

            is RunningSessionSideEffect.SetLocation -> {
                cameraPositionState.move(CameraUpdate.scrollTo(sideEffect.latLng))
            }
        }
    }

    if (uiState.sessionState !is RunningSessionState.Initial) {
        BackHandler {
            // 뒤로가기 못하게
        }
    }

    PermissionHandler(
        context = LocalContext.current,
        lifecycleOwner = LocalLifecycleOwner.current,
        permissionsToRequest = MapConstants.MAP_PERMISSIONS,
        onPermissionResult = { isGranted ->
            viewModel.onIntent(RunningSessionIntent.UpdatePermission(isGranted))
        },
    )

    Box(
        modifier =
            Modifier.fillMaxSize(),
    ) {
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
        )

        if (uiState.sessionState is RunningSessionState.Initial) {
            InitialContent(
                onStartClick = {
                    viewModel.onIntent(RunningSessionIntent.SessionStart)
                },
            )
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
