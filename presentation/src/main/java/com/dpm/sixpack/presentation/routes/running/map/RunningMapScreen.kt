package com.dpm.sixpack.presentation.routes.running.map

import android.graphics.Bitmap
import android.graphics.Matrix
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
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
import com.dpm.sixpack.presentation.routes.running.map.MapConstants.DEFAULT_ZOOM
import com.dpm.sixpack.presentation.routes.running.map.MapConstants.FINAL_RESOLUTION
import com.dpm.sixpack.presentation.routes.running.map.MapConstants.SNAPSHOT_PADDING
import com.dpm.sixpack.presentation.routes.running.map.component.LocationTrackingButton
import com.dpm.sixpack.presentation.routes.running.map.component.SheetDragState
import com.dpm.sixpack.presentation.routes.running.map.contract.MapIntent
import com.dpm.sixpack.presentation.routes.running.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.routes.running.map.contract.MapUiState
import com.dpm.sixpack.presentation.routes.running.map.contract.MapViewState
import com.dpm.sixpack.presentation.routes.running.map.friendsheet.DraggableFriendBottomSheet
import com.dpm.sixpack.presentation.routes.running.session.RunningSessionScreen
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CameraUpdateReason
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapEffect
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapType
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.math.roundToInt

private val sheetPeekHeight = 84.dp
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
                cameraPositionState.position = CameraPosition(sideEffect.latLng, DEFAULT_ZOOM)
            }

            is MapSideEffect.NavigateToReport -> {
                val sessionId = sideEffect.sessionId
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
    val graphicsLayer = rememberGraphicsLayer()

    val density = LocalDensity.current
    val sheetPeekHeightPx = with(density) { sheetPeekHeight.toPx() }
    val startButtonHeightPx = with(density) { startButtonHeightDp.toPx() }

    var naverMapInstance by remember {
        mutableStateOf<NaverMap?>(null)
    }

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
                        }.drawWithContent {
                            // 컨텐츠를 GraphicsLayer에 기록(record)합니다.
                            graphicsLayer.record {
                                // this@drawWithContent.drawContent()를 호출하여 Composable의 실제 내용(지도)을 그립니다.
                                this@drawWithContent.drawContent()
                            }

                            // 기록된 레이어를 화면에 그립니다. (이걸 해야 지도가 보임)
                            drawLayer(graphicsLayer)
                        },
                cameraPositionState = cameraPositionState,
                properties =
                    MapProperties(
                        minZoom = MapConstants.MIN_ZOOM_LEVEL,
                        maxZoom = MapConstants.MAX_ZOOM_LEVEL,
                        locationTrackingMode = LocationTrackingMode.NoFollow,
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
                contentPadding =
                    PaddingValues(
                        bottom =
                            if (mapState.mapViewState is MapViewState.Finishing) 0.dp else (boxHeight * 0.10).dp,
                    ),
            ) {
                MapEffect(Unit) { map ->
                    naverMapInstance = map
                }

                (mapState.mapViewState as? MapViewState.HasPathColorState)?.pathColorState?.let { mapUiState ->
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

            when (mapState.mapViewState) {
                MapViewState.Loading -> setFullScreenLoading(true)
                is MapViewState.Finishing -> {
                    setFullScreenLoading(true)

                    LaunchedEffect(Unit) {
                        // 1. 캡처 전에 naverMapInstance가 준비되었는지 확인
                        val mapInstance = naverMapInstance
                        if (mapInstance == null) {
                            Timber.e("NaverMap 객체가 null이라 캡처에 실패했습니다.")
                            // TODO: 캡처 실패 처리 (예: onMapIntent(MapIntent.SessionFinish(null)))
                            return@LaunchedEffect
                        }
                        mapInstance.locationOverlay.isVisible = false

                        val bounds = mapState.mapViewState.latLngBounds

                        // 2. 카메라 이동
                        cameraPositionState.move(CameraUpdate.fitBounds(bounds, SNAPSHOT_PADDING))

                        // 3. (핵심) 카메라가 멈출 때까지 대기
                        snapshotFlow { cameraPositionState.isMoving }
                            .filterNot { isMoving -> isMoving } // isMoving이 false가 될 때까지
                            .first() // false가 되면 통과

                        // 타일 로딩을 위한 아주 짧은 추가 대기
                        delay(200)

                        // 맵 객체로 직접 캡처 (awaitSnapshot 헬퍼 함수 사용)
                        val bitmap =
                            try {
                                mapInstance.awaitSnapshot()
                            } catch (e: Exception) {
                                Timber.e(e, "맵 스냅샷 캡처 중 오류 발생")
                                null
                            }

                        // 결과 처리
                        if (bitmap != null) {
                            onMapIntent(MapIntent.SessionFinish(bitmap))
                        } else {
                            Timber.e("캡처된 비트맵이 null입니다.")
                        }

                        mapInstance.locationOverlay.isVisible = true
                    }
                }

                is MapViewState.Friend -> {
                    setFullScreenLoading(false)
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
                        sheetHeight = sheetMaxHeight,
                        startButtonHeight = startButtonHeightDp,
                    )

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

                is MapViewState.Running -> {
                    RunningSessionScreen(
                        panelRef = panelRef,
                        updateNewRunningPath = { pathState ->
                            onMapIntent(MapIntent.UpdateRunningMapPath(pathState))
                        },
                        onSessionFinish = {
                            onMapIntent(MapIntent.ReadyToFinish)
                        },
                        setFullScreenLoading = setFullScreenLoading,
                    )
                }
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

                                    else -> {
                                        // do nothing
                                    }
                                }
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

/**
 * NaverMap.takeSnapshot(callback)을 suspend 함수로 변환하는 헬퍼 함수
 */
private suspend fun NaverMap.awaitSnapshot(): Bitmap =
    suspendCancellableCoroutine { continuation ->
        takeSnapshot { originalBitmap ->
            // 원본 비트맵 (직사각형)
            if (continuation.isActive) {
                try {
                    val width = originalBitmap.width
                    val height = originalBitmap.height
                    val cropSize = minOf(width, height)
                    val x = (width - cropSize) / 2
                    val y = (height - cropSize) / 2

                    // 해상도 제한(스케일링) 준비
                    // 원본의 정사각형 크기(cropSize)를 최종 해상도(FINAL_RESOLUTION)로 줄이기 위한 비율
                    val scale = FINAL_RESOLUTION.toFloat() / cropSize.toFloat()
                    val matrix = Matrix()
                    matrix.postScale(scale, scale)

                    // 최적화된 Config 설정
                    // ARGB_8888 (32비트) 대신 RGB_565 (16비트) 사용
                    val config = Bitmap.Config.RGB_565

                    // (크롭 + 스케일링 + Config 변경)을 한 번에 실행
                    // createBitmap 오버로드를 사용하여 메모리 효율적으로 처리
                    val optimizedBitmap =
                        Bitmap.createBitmap(
                            originalBitmap,
                            x,
                            y,
                            cropSize,
                            cropSize,
                            matrix,
                            true,
                        )

                    // createBitmap이 config를 무시할 경우(일부 기기), 수동으로 config 변경
                    val finalBitmap =
                        if (optimizedBitmap.config != config) {
                            val copiedBitmap = optimizedBitmap.copy(config, false)
                            optimizedBitmap.recycle()
                            copiedBitmap
                        } else {
                            optimizedBitmap
                        }

                    continuation.resume(finalBitmap)
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.cancel(CancellationException("비트맵 자르기 실패: ${e.message}"))
                    }
                }
            } else if (continuation.isActive) {
                // 원본 비트맵 자체가 null인 경우
                continuation.cancel(CancellationException("맵 스냅샷 비트맵이 null입니다."))
            }
        }
    }
