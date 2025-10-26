package com.dpm.sixpack.presentation.routes.terms.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.signup.ui.component.terms.TermsAgreementComponent
import com.dpm.sixpack.presentation.routes.terms.contract.TermsIntent
import com.dpm.sixpack.presentation.routes.terms.contract.TermsState
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun TermsAgreementScreen(
    state: TermsState,
    onIntent: (TermsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(TermsIntent.OnBackButtonClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = SixPackDimen.defaultSideMargin),
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = stringResource(R.string.signup_title_terms_agreement),
                    style = SixpackTheme.typography.h2Bold,
                    color = SixpackTheme.colors.gray900,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Terms Agreement Component
                TermsAgreementComponent(
                    termsState = state.termsState,
                    isAllTermsChecked = state.isAllTermsAgreed,
                    onToggleAllTerms = { onIntent(TermsIntent.OnAllTermsToggled(it)) },
                    onToggle = { termType, isChecked ->
                        onIntent(TermsIntent.OnTermToggled(termType, isChecked))
                    },
                )
            }

            // Bottom Button
            DoRunDefaultButton(
                text = stringResource(R.string.signup_title_terms_agreement_action),
                onClick = {
                    onIntent(TermsIntent.OnAgreeClick)
                },
                enabled = state.isAllRequiredTermsAgreed && !state.isLoading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .consumeWindowInsets(paddingValues)
                        .imePadding()
                        .padding(horizontal = SixPackDimen.defaultSideMargin)
                        .padding(bottom = 24.dp),
            )
        }
    }
}

@Preview
@Composable
private fun TermsAgreementScreenPreview() {
    DoRunPreviewWrapper {
        TermsAgreementScreen(
            state = TermsState(),
            onIntent = {},
        )
    }
}
