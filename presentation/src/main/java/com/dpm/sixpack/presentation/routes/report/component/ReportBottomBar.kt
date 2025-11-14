package com.dpm.sixpack.presentation.routes.report.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.report.contract.ReportBottomBarType
import com.dpm.sixpack.presentation.routes.report.contract.ReportIntent
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun ReportBottomBar(
    imageUrl: String?,
    bottomBarType: ReportBottomBarType,
    modifier: Modifier = Modifier,
    onIntent: (ReportIntent) -> Unit = {},
) {
    when (bottomBarType) {
        ReportBottomBarType.NONE -> {
            // nothing
        }

        ReportBottomBarType.DETAIL -> {
            ReportBottomBarToDetail(
                modifier = modifier,
                imageUrl = imageUrl ?: "",
                onClick = {
                    onIntent(ReportIntent.NavigateToPostDetail)
                },
            )
        }

        ReportBottomBarType.UPLOAD -> {
            ReportBottomBarToUpload(
                modifier = modifier,
                onClick = {
                    onIntent(ReportIntent.NavigateToPostUpload)
                },
            )
        }
    }
}

@Composable
internal fun ReportBottomBarToUpload(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    ReportBottomBarCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ill_feed_certification),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = stringResource(R.string.report_not_upload_yet),
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.gray500,
                    )
                    Text(
                        text = stringResource(R.string.report_not_upload_yet),
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.blue600,
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "go to upload button",
                tint = SixpackTheme.colors.gray500,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun ReportBottomBarToDetail(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    ReportBottomBarCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = stringResource(R.string.report_already_uploaded),
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.gray500,
                    )
                    Text(
                        text = stringResource(R.string.report_go_to_post_detail),
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.blue600,
                    )
                }
            }
            DoRunDefaultAsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(end = 8.dp)
                        .size(60.dp)
                        .clip(SixpackTheme.shapes.round8),
                placeholder = ColorPainter(SixpackTheme.colors.gray50),
                error = painterResource(R.drawable.ill_character_success),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun ReportBottomBarCard(
    modifier: Modifier = Modifier,
    shape: Shape = SixpackTheme.shapes.round16,
    colors: Color = SixpackTheme.colors.gray50,
    elevation: CardElevation = CardDefaults.cardElevation(0.dp),
    border: BorderStroke? = null,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier =
            modifier
                .background(color = colors, shape = shape)
                .clickable {
                    onClick()
                },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = colors),
        elevation = elevation,
        border = border,
    ) {
        content()
    }
}

@Preview
@Composable
private fun PreviewSessionDetailBottomBarUpload() {
    DoRunPreviewWrapper {
        ReportBottomBarToUpload()
    }
}

@Preview
@Composable
private fun PreviewSessionDetailBottomBarDetail() {
    DoRunPreviewWrapper {
        ReportBottomBarToDetail(
            imageUrl = " ",
        )
    }
}
