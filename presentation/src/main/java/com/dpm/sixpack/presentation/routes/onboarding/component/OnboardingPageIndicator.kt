package com.dpm.sixpack.presentation.routes.onboarding.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingPageIndicator(page: OnboardingPage, modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = SixpackTheme.colors.blue600)) {
            append(stringResource(R.string.onboarding_current_page_index, page.index))
        }
        withStyle(style = SpanStyle(color = SixpackTheme.colors.gray400)) {
            append(stringResource(R.string.onboarding_page_size, OnboardingPage.entries.size))
        }
    }
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = annotatedString,
            style = SixpackTheme.typography.b2Medium
        )
    }
}

enum class OnboardingPage(
    val index: Int,
) {
    PERMISSION(1),
    LEVEL(2),
    GOAL(3),
}

