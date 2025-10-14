package com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.finish

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun FinishScreenHeader(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.Companion.vectorResource(id = R.drawable.ill_goal_character),
            contentDescription = null,
            modifier = Modifier.Companion,
        )

        Spacer(modifier = Modifier.Companion.height(16.dp))

        Text(
            text = stringResource(R.string.onboarding_finish_title),
            style = SixpackTheme.typography.h2Bold,
            modifier =
                Modifier.Companion
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
        )
    }
}

@Preview
@Composable
private fun FinishScreenHeaderPreview() {
    DoRunPreviewWrapper {
        FinishScreenHeader()
    }
}
