package com.dpm.sixpack.presentation.routes.onboarding.ui.component.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixPackDimen

@Composable
fun OnboardingNavigationComponent(
    modifier: Modifier = Modifier,
    onClickSignUp: () -> Unit = {},
    onClickSignIn: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SixPackDimen.defaultSideMargin)
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DoRunDefaultButton(
            text = stringResource(R.string.onboarding_sign_up),
            onClick = onClickSignUp,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = buildAnnotatedString {
                val already = stringResource(R.string.onboarding_already_have_account) + " "
                val signin = stringResource(R.string.onboarding_signin)

                append(already)

                pushLink(LinkAnnotation.Clickable(tag = "login") { onClickSignIn() })
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(signin)
                }
                pop()
            },
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .padding(horizontal = SixPackDimen.defaultSideMargin)
                .padding(vertical = 4.dp)
        )
    }
}

@Preview
@Composable
private fun OnboardingNavigationComponentPreview() {
    DoRunPreviewWrapper {
        OnboardingNavigationComponent()
    }
}
