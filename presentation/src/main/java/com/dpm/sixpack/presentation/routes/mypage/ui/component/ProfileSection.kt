package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun ProfileSection(
    profileInfo: ProfileInfo,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Profile Image + Name + Friends
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Profile Image
            Box(
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SixpackTheme.colors.gray200),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_profile),
                    contentDescription = null,
                    tint = SixpackTheme.colors.gray600,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = profileInfo.nickname,
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray900,
                )
                Text(
                    text = "친구 ${profileInfo.friendCount}명",
                    style = SixpackTheme.typography.c1Regular,
                    color = SixpackTheme.colors.gray600,
                )
            }
        }

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            StatItem(
                label = "누적 거리",
                value = "${profileInfo.totalDistanceKm}km",
                modifier = Modifier.weight(1f),
            )
            StatItem(
                label = "인증 횟수",
                value = "${profileInfo.certificationCount}회",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600,
        )
        Text(
            text = value,
            style = SixpackTheme.typography.b1Bold,
            color = SixpackTheme.colors.gray900,
        )
    }
}

@Preview
@Composable
private fun ProfileSectionPreview() {
    DoRunPreviewWrapper {
        ProfileSection(
            profileInfo =
                ProfileInfo(
                    nickname = "두런두런",
                    friendCount = 7,
                    totalDistanceKm = 400.5,
                    certificationCount = 120,
                ),
        )
    }
}
