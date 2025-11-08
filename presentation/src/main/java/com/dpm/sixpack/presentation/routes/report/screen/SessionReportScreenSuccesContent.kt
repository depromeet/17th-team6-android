package com.dpm.sixpack.presentation.routes.report.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.domain.model.SessionDetail
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.components.record.RecordItem
import com.dpm.sixpack.presentation.common.util.format.toPostTimeStringOrNull
import com.dpm.sixpack.presentation.common.util.format.toPostTimeStringOrNullInstant
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatPaceToString
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import com.dpm.sixpack.presentation.routes.running.map.PaceColorCalculator.paceColorPoints
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun SessionReportScreenSuccessContent(
    sessionDetail: SessionDetail,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(SixpackTheme.colors.gray0), // 스크롤 시에도 배경색 유지
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
        Spacer(modifier = Modifier.height(30.dp))

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
        Spacer(modifier = Modifier.height(8.dp))

        // 빠름 <-> 느림 그라데이션 바
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
        ) {
            val pathColors = paceColorPoints.map { it.color }
            Text(
                text = "빠름",
                style = SixpackTheme.typography.b2Bold,
                color = pathColors[0],
            )
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(8.dp)
                        .padding(horizontal = 8.dp)
                        .background(
                            brush =
                                Brush.horizontalGradient(
                                    colors = pathColors,
                                ),
                            shape = RoundedCornerShape(40.dp),
                        ),
            )

            Text(
                text = "느림",
                style = SixpackTheme.typography.b2Bold,
                color = pathColors.last(),
            )
        }
    }
}
