package com.dpm.sixpack.presentation.routes.running.map.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.theme.SixpackTheme
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.MarkerDefaults
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMapComposable

/**
 * URL 프로필 이미지가 포함된 커스텀 마커 Composable
 *
 * @param state 마커의 상태 (위치 포함)
 * @param profileImageUrl http/https 형식의 사용자 프로필 이미지 URL
 * @param pinIconResId 배경이 될 핀 이미지의 drawable 리소스 ID (e.g., R.drawable.ic_custom_marker)
 * @param markerSize 마커의 전체 크기 (width/height)
 * @param profileImageSize 프로필 이미지가 표시될 크기
 */
@ExperimentalNaverMapApi
@Composable
@NaverMapComposable
internal fun DoRunMarker(
    userName: String,
    state: MarkerState,
    profileImageUrl: String,
    profileImageSize: Dp = 60.dp,
    @DrawableRes pinIconResId: Int = R.drawable.ill_marker_background,
) {
    val pinBackgroundSize = 80.dp
    val context = LocalContext.current

    MarkerComposable(
        keys = arrayOf(userName, profileImageUrl, pinIconResId, profileImageSize),
        state = state,
        // 중요: width/height를 Auto로 설정해야
        // content 람다의 Column(이름표 + 핀)의 실제 크기를
        // 그대로 마커 크기로 사용합니다.
        width = MarkerDefaults.SizeAuto,
        height = MarkerDefaults.SizeAuto,
        // 앵커는 기존과 동일하게 (0.5f, 1.0f)로 설정하여
        // Column의 가장 아랫부분(핀의 뾰족한 끝) 중앙에 좌표를 맞춥니다.
        anchor = Offset(0.5f, 1.0f),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 이름표
            Text(
                text = userName,
                modifier =
                    Modifier
                        .background(
                            color = Color.White,
                            shape = SixpackTheme.shapes.round8,
                        ).border(width = 1.dp, color = SixpackTheme.colors.gray50, shape = SixpackTheme.shapes.round8)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                color = SixpackTheme.colors.gray900,
                style = SixpackTheme.typography.b2Medium,
            )

            // (선택) 이름표와 핀 사이의 간격
            Spacer(modifier = Modifier.height(2.dp))

            Box(
                modifier =
                    Modifier
                        .padding(2.dp)
                        .size(pinBackgroundSize),
                contentAlignment = Alignment.Center,
            ) {
                // 배경 핀
                Image(
                    painter = painterResource(id = pinIconResId),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .border(
                                width = 1.dp, // 보더 두께
                                color = SixpackTheme.colors.gray50, // 보더 색상 (원하는 색상으로)
                                shape = PinShape(), // 👈 커스텀 Shape 적용
                            ).fillMaxSize(),
                )

                // 프로필 이미지
                DoRunDefaultAsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile",
                    modifier =
                        Modifier
                            .size(profileImageSize)
                            .clip(CircleShape)
                            .align(BiasAlignment(0f, -0.25f)),
                    error = painterResource(id = R.drawable.ill_profile_placeholder),
                )
            }
        }
    }
}

class PinShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            Path().apply {
                // SVG pathData를 Compose Path 명령어로 변환합니다.
                // M66,33C66,47.368 56.817,59.592 44,64.122C41.564,64.983 37.063,70.329 34.643,73.377C33.809,74.426 32.191,74.426 31.357,73.377C28.937,70.329 24.436,64.983 22,64.122C9.183,59.592 0,47.368 0,33C0,14.775 14.775,0 33,0C51.225,0 66,14.775 66,33Z

                // 주의: SVG pathData는 viewport 좌표계입니다.
                // size.width, size.height에 맞게 스케일링해야 합니다.
                // 원본 SVG의 viewportWidth="66", viewportHeight="75" 이므로,
                // 이 비율에 맞춰 Path 명령어를 작성해야 합니다.
                val scaleX = size.width / 66f
                val scaleY = size.height / 75f

                moveTo(66f * scaleX, 33f * scaleY)
                cubicTo(
                    66f * scaleX,
                    47.368f * scaleY,
                    56.817f * scaleX,
                    59.592f * scaleY,
                    44f * scaleX,
                    64.122f * scaleY,
                )
                cubicTo(
                    41.564f * scaleX,
                    64.983f * scaleY,
                    37.063f * scaleX,
                    70.329f * scaleY,
                    34.643f * scaleX,
                    73.377f * scaleY,
                )
                cubicTo(
                    33.809f * scaleX,
                    74.426f * scaleY,
                    32.191f * scaleX,
                    74.426f * scaleY,
                    31.357f * scaleX,
                    73.377f * scaleY,
                )
                cubicTo(
                    28.937f * scaleX,
                    70.329f * scaleY,
                    24.436f * scaleX,
                    64.983f * scaleY,
                    22f * scaleX,
                    64.122f * scaleY,
                )
                cubicTo(
                    9.183f * scaleX,
                    59.592f * scaleY,
                    0f * scaleX,
                    47.368f * scaleY,
                    0f * scaleX,
                    33f * scaleY,
                )
                cubicTo(
                    0f * scaleX,
                    14.775f * scaleY,
                    14.775f * scaleX,
                    0f * scaleY,
                    33f * scaleX,
                    0f * scaleY,
                )
                cubicTo(
                    51.225f * scaleX,
                    0f * scaleY,
                    66f * scaleX,
                    14.775f * scaleY,
                    66f * scaleX,
                    33f * scaleY,
                )
                close()
            }
        return Outline.Generic(path)
    }
}
