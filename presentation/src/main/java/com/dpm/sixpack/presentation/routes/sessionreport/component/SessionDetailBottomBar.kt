package com.dpm.sixpack.presentation.routes.sessionreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun SessionDetailBottomBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier =
            modifier
                .background(color = SixpackTheme.colors.gray50, shape = SixpackTheme.shapes.round16)
                .padding(all = 16.dp)
                .clickable {
                    onClick()
                },
        shape = SixpackTheme.shapes.round16,
        colors = CardDefaults.cardColors(containerColor = SixpackTheme.colors.gray50),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween, // 아이콘과 텍스트 양 끝 배치
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ill_feed_certification), // 임시 아이콘, 이미지에 맞는 아이콘으로 교체 필요
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "아직 인증하지 않았어요!",
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.gray500,
                    )
                    Text(
                        text = "이 기록 인증하러가기",
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.blue600,
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "인증하러 가기",
                tint = SixpackTheme.colors.gray500,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSessionDetailBottomBar() {
    DoRunPreviewWrapper {
        SessionDetailBottomBar()
    }
}
