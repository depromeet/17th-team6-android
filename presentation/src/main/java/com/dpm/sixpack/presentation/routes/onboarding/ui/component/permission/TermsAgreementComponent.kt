package com.dpm.sixpack.presentation.routes.onboarding.ui.component.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.permission.TermType
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.permission.item.TermRow
import com.dpm.sixpack.presentation.routes.onboarding.ui.component.permission.sub.AgreeAllTermsRow
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun TermsAgreementComponent(
    termsState: Map<TermType, Boolean>,
    modifier: Modifier = Modifier.Companion,
    isAllTermsChecked: Boolean = false,
    onToggleAllTerms: (Boolean) -> Unit = {},
    onToggle: (TermType, Boolean) -> Unit = { _, _ -> },
) {
    Column(modifier = modifier) {
        AgreeAllTermsRow(
            isChecked = isAllTermsChecked,
            onClickToggle = onToggleAllTerms,
        )
        Spacer(modifier = Modifier.Companion.height(20.dp))

        Spacer(
            modifier =
                Modifier.Companion
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = SixpackTheme.colors.gray50),
        )

        Spacer(modifier = Modifier.Companion.height(20.dp))

        TermType.entries.forEach { term ->
            TermRow(
                term = term,
                isChecked = termsState[term] ?: false,
                onClickToggle = { isChecked ->
                    onToggle(term, isChecked)
                },
            )

            Spacer(Modifier.Companion.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun TermsAgreementGroupPreview() {
    DoRunPreviewWrapper {
        TermsAgreementComponent(
            termsState =
                mapOf(
                    TermType.LOCATION to true,
                    TermType.MARKETING to false,
                    TermType.MARKETING to true,
                ),
            modifier = Modifier.Companion.padding(horizontal = 20.dp),
        )
    }
}
