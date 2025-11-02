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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun PostImageWithRecord(
    postImageUrl: String,
    runningSummary: RunningSummary,
    onPostImageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .noRippleClickable(onClick = onPostImageClick),
    ) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(postImageUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = "",
            modifier =
                Modifier
                    .fillMaxSize(),
            placeholder = ColorPainter(SixpackTheme.colors.gray0),
            error = ColorPainter(SixpackTheme.colors.gray200),
            contentScale = ContentScale.Crop,
        )

        PostTimeTextBox(
            postTime = runningSummary.recordDateTime,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 20.dp, start = 20.dp),
        )

        RunningSummaryOverlay(
            totalDistance = runningSummary.totalDistance,
            totalTime = runningSummary.totalTime,
            averagePace = runningSummary.averagePace,
            cadence = runningSummary.cadence,
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp, bottom = 20.dp, end = 20.dp),
        )
    }
}

@Composable
private fun RunningSummaryOverlay(
    totalDistance: String,
    totalTime: String,
    averagePace: String,
    cadence: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SummaryItem(
                title = stringResource(id = R.string.feed_post_image_record_distance),
                record = "${totalDistance}km",
                recordTextStyle = SixpackTheme.typography.h1Bold,
                modifier = Modifier.weight(1f),
            )
            SummaryItem(
                title = stringResource(id = R.string.feed_post_image_record_time),
                record = totalTime,
                recordTextStyle = SixpackTheme.typography.h1Bold,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SummaryItem(
                title = stringResource(id = R.string.feed_post_image_record_pace),
                record = averagePace,
                recordTextStyle = SixpackTheme.typography.t1Bold,
                modifier = Modifier.weight(1f),
            )
            SummaryItem(
                title = stringResource(id = R.string.feed_post_image_record_cadence),
                record = "${cadence}spm",
                recordTextStyle = SixpackTheme.typography.t1Bold,
                modifier = Modifier.weight(1f),
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
            color = SixpackTheme.colors.gray0,
        )
        Text(
            text = record,
            style = recordTextStyle,
            color = SixpackTheme.colors.gray0,
        )
    }
}

@Composable
fun PostTimeTextBox(
    postTime: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(
                    color = SixpackTheme.colors.gray900.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(33.dp),
                ).padding(
                    vertical = 6.dp,
                    horizontal = 12.dp,
                ),
    ) {
        Text(
            text = postTime,
            modifier = Modifier,
            color = SixpackTheme.colors.gray0,
            style = SixpackTheme.typography.c1Regular,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF666666)
@Composable
fun PostImageWithRecordPreview() {
    DoRunPreviewWrapper {
        val runningSummary =
            RunningSummary(
                totalDistance = "5.2",
                totalTime = "30분", // 30분
                averagePace = "5'30''",
                cadence = "160",
                recordDateTime = "2023-10-01 14:30",
            )

        Column {
            PostImageWithRecord(
                postImageUrl = "",
                runningSummary = runningSummary,
                modifier = Modifier.padding(16.dp),
                onPostImageClick = {},
            )
        }
    }
}
