package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.routes.session.component.LocationTrackingButton
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

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningSessionScreen(
    uiState: RunningSessionUiState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    onLocationChange: (LatLng) -> Unit,
    onTrackingButtonClick: (RunningSessionIntent.ToggleFollowingMode) -> Unit,
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
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (naverMapRef, panelRef, trackingButtonRef) = createRefs()

        NaverMap(
            modifier =
                Modifier
                    .constrainAs(naverMapRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
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
                    isLocationButtonEnabled = false,
                    isLogoClickEnabled = false,
                    isScaleBarEnabled = false,
                    isCompassEnabled = false,
                ),
            locationSource = locationSource,
            onLocationChange = { location ->
                if (uiState.isFollowingModeEnabled) {
                    onLocationChange(LatLng(location))
                }
            },
        ) {
            (uiState.sessionState as? RunningSessionState.HasMapPath)?.mapUiState?.let { mapUiState ->
                mapUiState.path.forEachIndexed { pathIndex, currentPath ->
                    if (currentPath.size > 1) {
                        val currentPathColors = mapUiState.paceColors[pathIndex]

                        for (pointIndex in 0 until currentPath.lastIndex - 1) {
                            key(pathIndex, pointIndex) {
                                val segmentCoords =
                                    listOf(
                                        currentPath[pointIndex],
                                        currentPath[pointIndex + 1],
                                    )
                                val segmentColor = Color(currentPathColors[pointIndex])

                                PathOverlay(
                                    coords = segmentCoords,
                                    color = segmentColor,
                                    width = 8.dp,
                                    outlineWidth = 0.dp,
                                )
                            }
                        }
                    }
                }
            }
        }

        LocationTrackingButton(
            isFollowing = uiState.isFollowingModeEnabled,
            onClick = {
                onTrackingButtonClick(RunningSessionIntent.ToggleFollowingMode)
            },
            modifier =
                Modifier
                    .constrainAs(trackingButtonRef) {
                        end.linkTo(parent.end, margin = 24.dp)

                        when (uiState.sessionState) {
                            is RunningSessionState.Initial -> {
                                bottom.linkTo(parent.bottom, margin = 100.dp)
                            }

                            !is RunningSessionState.ReadyState -> {
                                bottom.linkTo(panelRef.top, margin = 24.dp)
                            }

                            else -> {
                                bottom.linkTo(parent.bottom, margin = 24.dp)
                            }
                        }
                    },
        )

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
                    modifier =
                        Modifier.constrainAs(panelRef) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
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
                    modifier =
                        Modifier.constrainAs(panelRef) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
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
                                remainDistance = formatDistanceToKm(uiState.sessionState.remainingDistanceMeter),
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
