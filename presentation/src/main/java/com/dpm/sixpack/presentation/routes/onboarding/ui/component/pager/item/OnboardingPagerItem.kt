package com.dpm.sixpack.presentation.routes.onboarding.ui.component.pager.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.onboarding.ui.model.OnboardingPage
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingPagerItem(
    modifier: Modifier = Modifier,
    page: OnboardingPage,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier =
                Modifier
                    .size(268.dp)
                    .padding(bottom = 24.dp),
        )
        Text(
            text = stringResource(id = page.titleRes),
            style = SixpackTheme.typography.h2Bold,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SixPackDimen.defaultSideMargin)
                    .padding(bottom = 12.dp),
        )
        Text(
            text = stringResource(id = page.descriptionRes),
            style = SixpackTheme.typography.b1Regular,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SixPackDimen.defaultSideMargin),
        )
    }
}

@Preview
@Composable
private fun OnboardingPagerItemPreview() {
    DoRunPreviewWrapper {
        OnboardingPagerItem(
            page = OnboardingPage.Page1,
        )
    }
}
