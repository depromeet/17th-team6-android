package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.mypage.contract.CertificationStatus
import com.dpm.sixpack.presentation.routes.mypage.contract.RecordItem
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RecordCard(
    record: RecordItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = record.date,
            style = SixpackTheme.typography.b2Medium,
            color = SixpackTheme.colors.gray700,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SixpackTheme.colors.gray0)
                    .clickable(onClick = onClick)
                    .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = record.time,
                            style = SixpackTheme.typography.c1Regular,
                            color = SixpackTheme.colors.gray700,
                        )
                        record.certificationStatus?.let { status ->
                            CertificationBadge(status = status)
                        }
                    }

                    Text(
                        text = "${record.distanceKm}km",
                        style = SixpackTheme.typography.h2Bold,
                        color = SixpackTheme.colors.gray900,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RecordStat(label = "시간", value = record.durationFormatted)
                        RecordStat(label = "페이스", value = record.paceFormatted)
                        RecordStat(label = "케이던스", value = "${record.cadence} spm")
                    }
                }

                if (record.certificationStatus == CertificationStatus.COMPLETED) {
                    Icon(
                        painter = painterResource(R.drawable.ill_character_success),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(72.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CertificationBadge(
    status: CertificationStatus,
    modifier: Modifier = Modifier,
) {
    val (text, backgroundColor, textColor) =
        when (status) {
            CertificationStatus.AVAILABLE -> Triple("인증 가능", SixpackTheme.colors.blue100, SixpackTheme.colors.blue600)
            CertificationStatus.COMPLETED -> Triple("인증 완료", SixpackTheme.colors.gray100, SixpackTheme.colors.gray700)
        }

    Box(
        modifier =
            modifier
                .background(backgroundColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = SixpackTheme.typography.c1Regular,
            color = textColor,
        )
    }
}

@Composable
private fun RecordStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600,
        )
        Text(
            text = value,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray900,
        )
    }
}

@Preview
@Composable
private fun RecordCardCompletedPreview() {
    DoRunPreviewWrapper {
        RecordCard(
            record =
                RecordItem(
                    id = 1,
                    date = "2025.09.30 (화)",
                    time = "오전 10:11",
                    distanceKm = 8.02,
                    durationFormatted = "01:12:03",
                    paceFormatted = "6'74\"",
                    cadence = 128,
                    certificationStatus = CertificationStatus.COMPLETED,
                ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun RecordCardAvailablePreview() {
    DoRunPreviewWrapper {
        RecordCard(
            record =
                RecordItem(
                    id = 2,
                    date = "2025.10.01 (수)",
                    time = "오후 02:30",
                    distanceKm = 5.5,
                    durationFormatted = "00:45:20",
                    paceFormatted = "8'15\"",
                    cadence = 115,
                    certificationStatus = CertificationStatus.AVAILABLE,
                ),
            onClick = {},
        )
    }
}
