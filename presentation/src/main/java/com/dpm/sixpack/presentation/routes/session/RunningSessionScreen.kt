package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.routes.session.component.MapConstants
import com.dpm.sixpack.presentation.routes.session.component.dialog.CooldownStopDialog
import com.dpm.sixpack.presentation.routes.session.component.dialog.RunningStopDialog
import com.dpm.sixpack.presentation.routes.session.component.dialog.WarmUpSkipDialog
import com.dpm.sixpack.presentation.routes.session.component.dialog.WarmUpStopDialog
import com.dpm.sixpack.presentation.routes.session.component.panel.RunningRecordPanel
import com.dpm.sixpack.presentation.routes.session.component.ready.ReadyOverlay
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionIntent
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
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberFusedLocationSource

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionScreen(
    uiState: RunningSessionUiState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    onLocationChange: (LatLng) -> Unit,
    onPauseClick: (RunningSessionIntent.PauseIntent) -> Unit,
    onResumeClick: (RunningSessionIntent.ResumeIntent) -> Unit,
    onStopClick: (RunningSessionIntent.StopIntent) -> Unit,
    onStopCancelClick: (RunningSessionIntent.StopCancelIntent) -> Unit,
    onStopConfirmClick: (RunningSessionIntent.StopConfirmIntent) -> Unit,
    onSkipClick: () -> Unit,
    onSkipCancelClick: () -> Unit,
    onSkipConfirmClick: () -> Unit,
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
            // FIXME: Temporary Draw Path
            if (uiState.sessionState is RunningSessionState.Main.Running) {
                val mapUiState = uiState.sessionState.mapUiState
                mapUiState.path.forEachIndexed { index, line ->
                    if (line.size > 2) {
                        PathOverlay(
                            coords = line,
                            color = Color.Black,
                        )
                    }
                }
            }
        }

        when (uiState.sessionState) {
            is RunningSessionState.Initial -> {
                // do nothing
            }

            is RunningSessionState.ReadyState -> {
                ReadyOverlay(
                    modifier = Modifier.fillMaxSize(),
                    readyState = uiState.sessionState,
                )
            }

            is RunningSessionState.RunningState -> {
                RunningRecordPanel(
                    modifier = Modifier.fillMaxWidth(),
                    sessionState = uiState.sessionState,
                    onPauseClick = onPauseClick,
                    onResumeClick = onResumeClick,
                    onStopClick = { stopIntent ->
                        onStopClick(stopIntent)
                    },
                    onSkipClick = onSkipClick,
                )
            }

            is RunningSessionState.PausedState -> {
                RunningRecordPanel(
                    modifier = Modifier.fillMaxWidth(),
                    sessionState = uiState.sessionState,
                    onPauseClick = onPauseClick,
                    onResumeClick = onResumeClick,
                    onStopClick = onStopClick,
                    onSkipClick = onSkipClick,
                )
                if (uiState.sessionState is RunningSessionState.WarmUp.Pause &&
                    uiState.sessionState.showSkipConfirmDialog
                ) {
                    WarmUpSkipDialog(
                        onCancelClick = onSkipCancelClick,
                        onStopConfirmClick = onSkipConfirmClick,
                    )
                }

                if (uiState.sessionState.showStopSessionConfirmDialog) {
                    when (uiState.sessionState) {
                        is RunningSessionState.WarmUp.Pause -> {
                            WarmUpStopDialog(
                                onCancelClick = {
                                    onStopCancelClick(RunningSessionIntent.WarmUpStopCancel)
                                },
                                onStopConfirmClick = {
                                    onStopConfirmClick(RunningSessionIntent.WarmUpStopConfirm)
                                },
                            )
                        }

                        is RunningSessionState.Main.Pause -> {
                            RunningStopDialog(
                                // FIXME: 실제 남은거리로 수정, 지금 목표거리임
                                remainDistance = uiState.sessionState.goalDistance,
                                onCancelClick = {
                                    onStopCancelClick(RunningSessionIntent.MainRunningStopCancel)
                                },
                                onStopConfirmClick = {
                                    onStopConfirmClick(RunningSessionIntent.MainRunningStopConfirm)
                                },
                            )
                        }

                        is RunningSessionState.CoolDown.Pause -> {
                            CooldownStopDialog(
                                onCancelClick = {
                                    onStopCancelClick(RunningSessionIntent.CoolDownStopCancel)
                                },
                                onStopClick = {
                                    onStopConfirmClick(RunningSessionIntent.CoolDownStopConfirm)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Preview
@Composable
private fun PreviewRunningSessionScreenContent() {
    RunningSessionScreen(
        uiState =
            RunningSessionUiState(
                sessionState = RunningSessionState.WarmUp.Ready(),
            ),
        cameraPositionState = CameraPositionState(),
        locationSource = rememberFusedLocationSource(),
        onLocationChange = { },
        onPauseClick = { },
        onResumeClick = { },
        onStopClick = { },
        onStopCancelClick = {},
        onStopConfirmClick = { },
        onSkipClick = { },
        onSkipCancelClick = {},
        onSkipConfirmClick = { },
    )
}
