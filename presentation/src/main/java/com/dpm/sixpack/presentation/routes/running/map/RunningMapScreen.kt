package com.dpm.sixpack.presentation.routes.running.map

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.util.PermissionHandler
import com.dpm.sixpack.presentation.routes.running.map.component.LocationTrackingButton
import com.dpm.sixpack.presentation.routes.running.map.contract.MapIntent
import com.dpm.sixpack.presentation.routes.running.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.routes.running.map.contract.MapUiState
import com.dpm.sixpack.presentation.routes.running.map.contract.MapViewState
import com.dpm.sixpack.presentation.routes.running.session.RunningSessionScreen
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CameraUpdateReason
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun RunningMapScreen(
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = hiltViewModel(),
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    navigateToReport: () -> Unit,
) {
    val mapState by mapViewModel.collectAsState()

    mapViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MapSideEffect.SetBottomBarVisibility -> {
                onBottomBarVisibilityChange(sideEffect.isVisible)
            }

            is MapSideEffect.SetCameraPosition -> {
                cameraPositionState.move(CameraUpdate.scrollTo(sideEffect.latLng))
            }

            MapSideEffect.NavigateToReport -> {
                onBottomBarVisibilityChange(true)
                navigateToReport()
            }
        }
    }

    PermissionHandler(
        context = LocalContext.current,
        lifecycleOwner = LocalLifecycleOwner.current,
        permissionsToRequest = MapConstants.MAP_PERMISSIONS,
        onPermissionResult = { isGranted ->
            mapViewModel.onIntent(MapIntent.UpdatePermission(isGranted))
        },
    )

    RunningMapScreenContent(
        modifier = modifier,
        mapState = mapState,
        cameraPositionState = cameraPositionState,
        locationSource = locationSource,
        onMapIntent = mapViewModel::onIntent,
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun RunningMapScreenContent(
    mapState: MapUiState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    onMapIntent: (MapIntent) -> Unit,
    modifier: Modifier,
) {
    LaunchedEffect(cameraPositionState.cameraUpdateReason) {
        val reason = cameraPositionState.cameraUpdateReason
        val reasonText =
            when (reason) {
                CameraUpdateReason.GESTURE -> {
                    onMapIntent(MapIntent.FollowingModeOff)
                }

                else -> {
                    // do noting
                }
            }
    }

    ConstraintLayout(modifier = modifier) {
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
                onMapIntent(MapIntent.UpdateUserLocation(LatLng(location)))
            },
        ) {
            (mapState.mapViewState as? MapViewState.Running)?.pathColorState?.let { mapUiState ->
                mapUiState.paths.forEachIndexed { pathIndex, currentPath ->
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
            isFollowing = mapState.isFollowingModeEnabled,
            onClick = {
                onMapIntent(MapIntent.ToggleFollowingMode)
            },
            modifier =
                Modifier
                    .constrainAs(trackingButtonRef) {
                        end.linkTo(parent.end, margin = 24.dp)

                        when (mapState.mapViewState) {
                            is MapViewState.Loading -> {
                                bottom.linkTo(parent.bottom, margin = 100.dp)
                            }

                            is MapViewState.Friend -> {
                                bottom.linkTo(parent.bottom, margin = 100.dp)
                            }

                            is MapViewState.Running -> {
                                bottom.linkTo(panelRef.top, margin = 24.dp)
                            }
                        }
                    },
        )

        when (mapState.mapViewState) {
            MapViewState.Loading -> {
            }

            is MapViewState.Running -> {
                RunningSessionScreen(
                    panelRef = panelRef,
                    updateNewRunningPath = { pathState ->
                        onMapIntent(MapIntent.UpdateRunningMapPath(pathState))
                    },
                    onSessionFinished = {
                        onMapIntent(MapIntent.SessionFinished)
                    },
                )
            }

            is MapViewState.Friend -> {}
        }
    }

    if (mapState.mapViewState !is MapViewState.Running) {
        InitialContent(
            onStartClick = {
                onMapIntent(MapIntent.SessionStartClick)
            },
        )
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
