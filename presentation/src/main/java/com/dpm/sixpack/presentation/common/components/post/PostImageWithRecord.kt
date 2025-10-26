package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.RunningSummaryUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun PostImageWithRecord(
    postImageUrl: String,
    runningSummary: RunningSummaryUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(postImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize(),
            placeholder = ColorPainter(SixpackTheme.colors.gray0),
            error = ColorPainter(SixpackTheme.colors.gray200),
            contentScale = ContentScale.Crop
        )

        PostTimeTextBox(
            postTime = runningSummary.postTime,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 20.dp)
        )

        RunningSummaryOverlay(
            runningSummary = runningSummary,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp, end = 20.dp)
        )
    }
}

@Composable
private fun RunningSummaryOverlay(
    runningSummary: RunningSummaryUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SummaryItem(
                title = "달린 거리",
                record = "${runningSummary.totalDistance}km",
                recordTextStyle = SixpackTheme.typography.h1Bold,
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                title = "달린 시간",
                record = "${runningSummary.totalRunTime}",
                recordTextStyle = SixpackTheme.typography.h1Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                title = "평균 페이스",
                record = runningSummary.averagePace,
                recordTextStyle = SixpackTheme.typography.t1Bold,
                modifier = Modifier.weight(1f)
            )
            SummaryItem(
                title = "케이던스",
                record = "${runningSummary.cadence}spm",
                recordTextStyle = SixpackTheme.typography.t1Bold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun SummaryItem(
    title: String,
    record: String,
    recordTextStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = title,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray0
        )
        Text(
            text = record,
            style = recordTextStyle,
            color = SixpackTheme.colors.gray0
        )
    }
}

@Composable
fun PostTimeTextBox(postTime: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = SixpackTheme.colors.gray900.copy(alpha = 0.4f),
                shape = RoundedCornerShape(33.dp)
            )
            .padding(
                vertical = 6.dp, horizontal = 12.dp
            )
    ) {
        Text(
            text = postTime,
            modifier = Modifier,
            color = SixpackTheme.colors.gray0,
            style = SixpackTheme.typography.c1Regular
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
fun PostImageWithRecordPreview() {
    DoRunPreviewWrapper {
        val runningSummary = RunningSummaryUiState(
            postTime = "1시간 전",
            totalDistance = 5.2,
            totalRunTime = 1800, // 30분
            averagePace = "5'30''",
            cadence = 160
        )

        Column {
            PostImageWithRecord(
                postImageUrl = "", // Preview에서는 URL이 비어있어도 됩니다.
                runningSummary = runningSummary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

