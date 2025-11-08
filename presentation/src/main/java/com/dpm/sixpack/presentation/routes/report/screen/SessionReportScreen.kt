package com.dpm.sixpack.presentation.routes.report.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.domain.model.SessionDetailFeed
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.record.RecordItem
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.format.toPostTimeStringOrNull
import com.dpm.sixpack.presentation.common.util.format.toPostTimeStringOrNullInstant
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatPaceToString
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import com.dpm.sixpack.presentation.routes.report.component.ReportBottomBar
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.routes.report.contract.ReportState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun SessionReportScreen(
    sessionId: Long,
    state: ReportState,
    onIntent: (ReportIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = {
                    onIntent(ReportIntent.NavigateBack)
                },
                titleContent = {
                },
            )
        },
        bottomBar = {
            if (state is ReportState.Success) {
                ReportBottomBar(
                    modifier = Modifier.padding(all = 24.dp),
                    onClick = {
                        onIntent(ReportIntent.NavigateToPostEdit(sessionId))
                    },
                )
            }
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        when (state) {
            ReportState.Loading -> {
//                FullScreenLoadingIndicator(
//                    alpha = 0.3f,
//                )
            }

            is ReportState.Success -> {
                val sessionDetail = state.sessionDetail
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(all = 24.dp)
                            .background(SixpackTheme.colors.gray0), // 스크롤 시에도 배경색 유지
                ) {
                    // 날짜 및 시간 정보
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = SixpackTheme.colors.blue600,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text =
                                sessionDetail.finishedAt.toPostTimeStringOrNullInstant()
                                    ?: sessionDetail.finishedAt.toPostTimeStringOrNull() ?: "",
                            style = SixpackTheme.typography.b2Medium,
                            color = SixpackTheme.colors.gray700,
                        )
                    }

                    // 러닝 기록 통계 카드
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(color = SixpackTheme.colors.gray50, shape = SixpackTheme.shapes.round16)
                                .padding(all = 20.dp),
                        shape = SixpackTheme.shapes.round16,
                        colors = CardDefaults.cardColors(containerColor = SixpackTheme.colors.gray50),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                        ) {
                            // 왼쪽 열
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                RecordItem(
                                    label = stringResource(R.string.record_total_distance),
                                    recordValue = formatDistanceToKm(sessionDetail.distanceTotal),
                                    emphasize = true,
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                RecordItem(
                                    label = stringResource(R.string.record_average_pace),
                                    recordValue = formatPaceToString(sessionDetail.paceAvg),
                                )
                            }
                            // 오른쪽 열
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                RecordItem(
                                    label = stringResource(R.string.record_total_duration),
                                    recordValue = formatSecondsToTime(sessionDetail.durationTotal),
                                    emphasize = true,
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                RecordItem(
                                    label = stringResource(R.string.record_average_cadence),
                                    recordValue = "${sessionDetail.cadenceAvg} spm",
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // 지도 이미지
                    DoRunDefaultAsyncImage(
                        model = sessionDetail.mapImage,
                        contentDescription = "러닝 경로 지도",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(
                                    ratio = 1.0f,
                                ).clip(SixpackTheme.shapes.round16),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 빠름 <-> 느림 그라데이션 바 (주석 처리)
                    // Modifier.fillMaxWidth().height(20.dp).padding(horizontal = 16.dp).background(Brush.horizontalGradient(...))
                }
            }

            ReportState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.data_load_failed),
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray700,
                    )

                    TextButton(
                        onClick = {
                            onIntent(ReportIntent.LoadSessionDetail(sessionId))
                        },
                        colors =
                            ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = SixpackTheme.colors.gray700,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent,
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(all = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.request_retry),
                                style = SixpackTheme.typography.b1Regular,
                                color = SixpackTheme.colors.gray700,
                            )
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = SixpackTheme.colors.gray700,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RunningRecordDetailScreenPreview() {
    DoRunPreviewWrapper {
        val sampleSessionDetail =
            SessionDetail(
                id = 1,
                createdAt = "2025-10-09T10:11:00Z",
                updatedAt = "2025-10-09T10:11:00Z",
                finishedAt = "2025-10-09T10:11:00Z",
                distanceTotal = 8020, // 8.02km
                durationTotal = 6726, // 1시간 52분 6초
                paceAvg = 450, // 7분 30초/km
                paceMax = 300,
                paceMaxLatitude = 0.0,
                paceMaxLongitude = 0.0,
                cadenceAvg = 144,
                cadenceMax = 180,
                mapImage = "https://i.ibb.co/L5k6h6f/map-example.png", // 실제 이미지 URL
                feed = null, // 인증하지 않은 상태
                segments = emptyList(),
            )

        // 인증한 상태 프리뷰 (SessionDetailFeed가 null이 아님)
        val sampleSessionDetailWithFeed =
            sampleSessionDetail.copy(
                feed =
                    SessionDetailFeed(
                        id = 1,
                        mapImage = "...",
                        selfieImage = "...",
                        content = "오늘 러닝 최고!",
                        createdAt = "...",
                    ),
            )

        Column {
//            RunningRecordDetailScreen(
//                sessionDetail = sampleSessionDetail,
//                onNavigateBack = {},
//                onNavigateToCertification = {},
//            )
//            Spacer(modifier = Modifier.height(20.dp))
            SessionReportScreen(
                sessionId = 123,
                state =
                    ReportState.Success(
                        sessionDetail = sampleSessionDetailWithFeed,
                    ),
                onIntent = { /* Handle intents */ },
            )
        }
    }
}
