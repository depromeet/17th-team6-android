package com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.screen.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.contract.uistate.permission.TermType
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.common.OnboardingNextButton
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.common.OnboardingPage
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.common.OnboardingPageIndicator
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.permission.TermsAgreementComponent
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingPermissionScreen(
    uiState: OnboardingUiState,
    modifier: Modifier = Modifier,
    onToggleAllTerms: (Boolean) -> Unit = {},
    onToggleTerm: (type: TermType, isChecked: Boolean) -> Unit = { _, _ -> },
    onClickNextButton: () -> Unit = {},
    onClickBackButton: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = SixpackTheme.colors.gray0),
    ) {
        DoRunNavigationTopBar(
            navigateToBack = onClickBackButton,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            OnboardingPageIndicator(page = OnboardingPage.PERMISSION)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.onboarding_permission_title),
                style = SixpackTheme.typography.h2Bold,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(40.dp))

            TermsAgreementComponent(
                termsState = uiState.termsState,
                isAllTermsChecked = uiState.isAllTermsChecked,
                onToggleAllTerms = onToggleAllTerms,
                onToggle = onToggleTerm,
                modifier = Modifier,
            )

            Spacer(Modifier.weight(1f))

            OnboardingNextButton(
                onClick = onClickNextButton,
                enabled = uiState.isPermissionNextEnabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPermissionScreenPreview() {
    DoRunPreviewWrapper {
        OnboardingPermissionScreen(
            uiState = OnboardingUiState(),
        )
    }
}
