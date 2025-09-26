package com.dpm.sixpack.presentation.routes.onboarding.ui.component.level

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.LevelType
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun LevelCard(
    modifier: Modifier = Modifier,
    level: LevelType = LevelType.BEGINNER,
    isSelected: Boolean = false,
    onSelectLevel: (LevelType) -> Unit = {},
) {
    val borderColor =
        if (isSelected) {
            SixpackTheme.colors.blue600
        } else {
            SixpackTheme.colors.gray100
        }

    val borderShape = SixpackTheme.shapes.round16

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = borderColor, shape = borderShape)
                .noRippleClickable(onClick = { onSelectLevel(level) }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(16.dp))

        Image(
            imageVector = ImageVector.vectorResource(level.img),
            contentDescription = null,
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
        ) {
            Text(
                text = stringResource(level.title),
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
            )

            Spacer(Modifier.width(4.dp))

            Text(
                text = stringResource(level.subTitle),
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray600,
            )
        }
    }
}

@Preview
@Composable
private fun LevelCardPreview() {
    DoRunPreviewWrapper {
        LevelCard(
            level = LevelType.BEGINNER,
            isSelected = false,
            onSelectLevel = {},
        )
    }
}
