package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.mypage.contract.YearMonth
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun MonthNavigation(
    yearMonth: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    canGoPrevious: Boolean = true,
    canGoNext: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onPreviousClick,
            enabled = canGoPrevious,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_left),
                contentDescription = "이전 달",
                tint =
                    if (canGoPrevious) {
                        SixpackTheme.colors.gray900
                    } else {
                        SixpackTheme.colors.gray300
                    },
            )
        }

        Text(
            text = yearMonth.format(),
            style = SixpackTheme.typography.b1Medium,
            color = SixpackTheme.colors.gray900,
            modifier = Modifier.padding(horizontal = 12.dp),
        )

        IconButton(
            onClick = onNextClick,
            enabled = canGoNext,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                contentDescription = "다음 달",
                tint =
                    if (canGoNext) {
                        SixpackTheme.colors.gray900
                    } else {
                        SixpackTheme.colors.gray300
                    },
            )
        }
    }
}

@Preview
@Composable
private fun MonthNavigationPreview() {
    DoRunPreviewWrapper {
        MonthNavigation(
            yearMonth = YearMonth(year = 2025, month = 10),
            onPreviousClick = {},
            onNextClick = {},
            canGoPrevious = true,
            canGoNext = true,
        )
    }
}

@Preview
@Composable
private fun MonthNavigationDisabledPreviousPreview() {
    DoRunPreviewWrapper {
        MonthNavigation(
            yearMonth = YearMonth(year = 2025, month = 10),
            onPreviousClick = {},
            onNextClick = {},
            canGoPrevious = false,
            canGoNext = true,
        )
    }
}

@Preview
@Composable
private fun MonthNavigationDisabledNextPreview() {
    DoRunPreviewWrapper {
        MonthNavigation(
            yearMonth = YearMonth(year = 2025, month = 10),
            onPreviousClick = {},
            onNextClick = {},
            canGoPrevious = true,
            canGoNext = false,
        )
    }
}
