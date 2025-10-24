package com.dpm.sixpack.presentation.routes.sessionreport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // 수정 1: androidx.compose.ui.graphics.Color로 변경
import androidx.compose.ui.graphics.lerp // 수정 1: Color를 위한 lerp import
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTopBarSlot
import com.dpm.sixpack.presentation.common.util.Sungsoo
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatPaceToString
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.session.component.panel.RecordItem
import com.dpm.sixpack.presentation.theme.SixpackTheme
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ColorPart
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.MultipartPathOverlay // 수정 2: compose용 MultipartPathOverlay로 변경
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState

@Composable
fun SessionReportRoute(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 스크롤 추가
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        DoRunTopBarSlot(
            trailingContent = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        modifier
                            .size(44.dp)
                            .noRippleClickable(onClick = navigateBack),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = SixpackTheme.colors.gray800,
                    )
                }
            },
        )

        Spacer(Modifier.height(24.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ill_character_success),
                contentDescription = null,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "목표한 러닝을 완료했어요",
                style = SixpackTheme.typography.t1Bold,
                color = SixpackTheme.colors.gray900,
            )

            Text(
                text = "오늘도 목표를 이룬 당신 멋져요!",
                style = SixpackTheme.typography.b1Regular,
                color = SixpackTheme.colors.gray600,
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .background(color = SixpackTheme.colors.blue100, shape = SixpackTheme.shapes.round16)
                        .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                // 왼쪽 열
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    RecordItem(
                        label = "총 거리",
                        recordValue = formatDistanceToKm(5100),
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    RecordItem(
                        label = "평균 페이스",
                        recordValue = formatPaceToString(450),
                    )
                }
                // 오른쪽 열
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    RecordItem(
                        label = "총 달린 시간",
                        recordValue = formatSecondsToTime(4440),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    RecordItem(
                        label = stringResource(R.string.record_cadence),
                        recordValue = "144",
                    )
                }
            }
            Spacer(Modifier.height(24.dp))

            RunningCourseMap(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 5f)
                        .clip(SixpackTheme.shapes.round16),
                path = Sungsoo,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun RunningCourseMap(
    modifier: Modifier = Modifier,
    path: List<LatLng>,
) {
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(path) {
        if (path.isNotEmpty()) {
            val bounds = LatLngBounds.Builder().include(path).build()
            cameraPositionState.move(CameraUpdate.fitBounds(bounds, 100))
        }
    }

    NaverMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings =
            MapUiSettings(
                isScrollGesturesEnabled = false,
                isZoomGesturesEnabled = false,
                isRotateGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isStopGesturesEnabled = false,
                isLogoClickEnabled = false,
                isCompassEnabled = false,
                isScaleBarEnabled = false,
                isZoomControlEnabled = false,
                isLocationButtonEnabled = false,
            ),
    ) {
        if (path.size > 1) {
            // 각 경로 조각의 색상을 정의합니다.
            val ppath: List<List<LatLng>> = path.chunked(10)

            val colorParts =
                path.windowed(2).mapIndexed { index, _ ->
                    val fraction = index.toFloat() / (path.size - 2).toFloat()
                    // 지나갈 경로: 파란색 -> 빨간색 그라데이션
                    val interpolatedColor = lerp(Color.Blue, Color.Red, fraction)

                    // ColorPart 생성 시 passedColor를 회색으로 지정합니다.
                    ColorPart(
                        color = interpolatedColor,
                        outlineColor = Color.Transparent,
                        passedColor = Color.Gray, // 지나온 경로의 색상은 회색
                        passedOutlineColor = Color.Transparent,
                    )
                }

            MultipartPathOverlay(
                coordParts = ppath,
                width = 8.dp,
                colorParts = colorParts,
                progress = 0.5, // ✅ 전체 경로의 50% 지점까지를 '지나온 경로'로 설정
            )
        }
    }
}

@Preview
@Composable
private fun adsfadsf() {
    DoRunPreviewWrapper {
        SessionReportRoute(navigateBack = { }, modifier = Modifier.fillMaxSize())
    }
}
