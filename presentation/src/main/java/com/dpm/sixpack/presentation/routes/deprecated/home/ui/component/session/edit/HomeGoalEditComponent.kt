package com.dpm.sixpack.presentation.routes.deprecated.home.ui.component.session.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomeGoalEditComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .widthIn(min = 335.dp)
                .background(
                    color = SixpackTheme.colors.gray0,
                    shape = SixpackTheme.shapes.round16,
                ).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ill_goal_edit_character),
            contentDescription = "목표 편집 일러스트", // TODO: 접근성 resource 추가
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(R.string.home_goal_edit_description),
            style = SixpackTheme.typography.b1Medium,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center,
        )

        DoRunDefaultButton(
            modifier =
                Modifier
                    .heightIn(min = 56.dp)
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            text = stringResource(R.string.home_goal_edit),
            onClick = onClick,
        )
    }
}

@Preview
@Composable
private fun HomeGoalEditComponentPreview() {
    DoRunPreviewWrapper {
        HomeGoalEditComponent()
    }
}
