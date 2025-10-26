package com.dpm.sixpack.presentation.routes.running.map

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.util.PermissionHandler
import com.dpm.sixpack.presentation.routes.freind.sampleFriendList
import com.dpm.sixpack.presentation.routes.running.map.component.DraggableFriendBottomSheet
import com.dpm.sixpack.presentation.routes.running.map.component.LocationTrackingButton
import com.dpm.sixpack.presentation.routes.running.map.component.SheetDragState
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
import kotlin.math.roundToInt

private val sheetPeekHeight = 72.dp
private val startButtonHeightDp = 72.dp

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun RunningMapScreen(
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = hiltViewModel(),
    setFullScreenLoading: (Boolean) -> Unit,
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
        setFullScreenLoading = setFullScreenLoading,
        onMapIntent = mapViewModel::onIntent,
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun RunningMapScreenContent(
    mapState: MapUiState,
    cameraPositionState: CameraPositionState,
    locationSource: LocationSource,
    setFullScreenLoading: (Boolean) -> Unit,
    onMapIntent: (MapIntent) -> Unit,
    modifier: Modifier,
) {
    val density = LocalDensity.current
    val sheetPeekHeightPx = with(density) { sheetPeekHeight.toPx() }
    val startButtonHeightPx = with(density) { startButtonHeightDp.toPx() }

    val draggableState =
        remember {
            AnchoredDraggableState(
                initialValue = SheetDragState.Collapsed,
            )
        }

    LaunchedEffect(cameraPositionState.cameraUpdateReason) {
        val reason = cameraPositionState.cameraUpdateReason
        if (reason == CameraUpdateReason.GESTURE) {
            onMapIntent(MapIntent.FollowingModeOff)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxHeight = with(density) { constraints.maxHeight.toFloat() }

        var guidelineFraction by remember { mutableFloatStateOf(0.1f) }

        // 시트의 최대 높이를 저장할 변수
        var sheetMaxHeight by remember { mutableStateOf(0.dp) }

        LaunchedEffect(draggableState.offset) {
            val yOffset = draggableState.offset
            if (!yOffset.isNaN()) {
                guidelineFraction = yOffset / boxHeight
            }
        }

        LaunchedEffect(boxHeight, startButtonHeightPx) {
            val collapsedOffset = (boxHeight - sheetPeekHeightPx) - startButtonHeightPx
            val halfExpandedOffset = (boxHeight / 1.8f) - startButtonHeightPx

            sheetMaxHeight = with(density) { (boxHeight - halfExpandedOffset).toDp() }

            draggableState.updateAnchors(
                DraggableAnchors {
                    SheetDragState.Collapsed at collapsedOffset
                    SheetDragState.HalfExpanded at halfExpandedOffset
                },
            )
        }

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (naverMapRef, panelRef, sheetRef, startButtonRef, trackingButtonRef) = createRefs()

            // LocationTrackingButton 위치 설정위한 가이드라인
            val sheetTopGuideline = createGuidelineFromTop(guidelineFraction)

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
                        logoGravity = Gravity.START,
                    ),
                locationSource = locationSource,
                onLocationChange = { location ->
                    onMapIntent(MapIntent.UpdateUserLocation(LatLng(location)))
                },
                contentPadding = PaddingValues(bottom = (boxHeight * 0.10).dp),
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

            if (mapState.mapViewState is MapViewState.Loading) {
                setFullScreenLoading(true)
            } else {
                setFullScreenLoading(false)
            }

            if (mapState.mapViewState is MapViewState.Running) {
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

            if (mapState.mapViewState !is MapViewState.Loading) {
                LocationTrackingButton(
                    isFollowing = mapState.isFollowingModeEnabled,
                    onClick = {
                        onMapIntent(MapIntent.ToggleFollowingMode)
                    },
                    modifier =
                        Modifier
                            .constrainAs(trackingButtonRef) {
                                end.linkTo(parent.end, margin = 12.dp)

                                when (mapState.mapViewState) {
                                    is MapViewState.Friend -> {
                                        bottom.linkTo(sheetTopGuideline, margin = 20.dp)
                                    }

                                    is MapViewState.Running -> {
                                        bottom.linkTo(panelRef.top, margin = 24.dp)
                                    }

                                    MapViewState.Loading -> { // do nothing
                                    }
                                }
                            },
                )
            }

            if (mapState.mapViewState is MapViewState.Friend) {
                DraggableFriendBottomSheet(
                    modifier =
                        Modifier
                            .constrainAs(sheetRef) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }.fillMaxWidth()
                            .offset {
                                val yOffset = draggableState.offset
                                if (yOffset.isNaN()) {
                                    IntOffset(x = 0, y = boxHeight.roundToInt())
                                } else {
                                    IntOffset(x = 0, y = yOffset.roundToInt())
                                }
                            },
                    draggableState = draggableState,
                    friendList = sampleFriendList,
                    sheetHeight = sheetMaxHeight,
                    startButtonHeight = startButtonHeightDp,
                )
            }

            if (mapState.mapViewState !is MapViewState.Running) {
                RunningStartButton(
                    modifier =
                        Modifier.constrainAs(startButtonRef) {
                            bottom.linkTo(parent.bottom)
                        },
                    onStartClick = {
                        onMapIntent(MapIntent.SessionStartClick)
                    },
                )
            }
        }
    }
}

@Composable
private fun RunningStartButton(
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(horizontal = 24.dp, vertical = 8.dp),
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
