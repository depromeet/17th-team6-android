package com.dpm.sixpack.presentation.routes.onboarding.permission

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.routes.onboarding.component.OnboardingNextButton
import com.dpm.sixpack.presentation.routes.onboarding.component.OnboardingPage
import com.dpm.sixpack.presentation.routes.onboarding.component.OnboardingPageIndicator
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.uistate.OnboardingPermissionUiState
import com.dpm.sixpack.presentation.routes.onboarding.permission.contract.uistate.TermType
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingPermissionScreen(
    uiState: State<OnboardingPermissionUiState>,
    onToggleAllTerms: (Boolean) -> Unit,
    onToggleTerm: (type: TermType, isChecked: Boolean) -> Unit,
    onClickNextButton: () -> Unit,
    onClickBackButton: () -> Unit,
    modifier: Modifier = Modifier,
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

            TermsAgreementGroup(
                termsState = uiState.value.termsState,
                isAllTermsChecked = uiState.value.isAllTermsChecked,
                onToggleAllTerms = onToggleAllTerms,
                onToggle = onToggleTerm,
                modifier = Modifier,
            )

            Spacer(Modifier.weight(1f))

            OnboardingNextButton(
                onClick = onClickNextButton,
                enabled = uiState.value.isNextButtonEnabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TermsAgreementGroup(
    termsState: Map<TermType, Boolean>,
    isAllTermsChecked: Boolean,
    onToggleAllTerms: (Boolean) -> Unit,
    onToggle: (TermType, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AgreeAllTermsRow(
            isChecked = isAllTermsChecked,
            onClickToggle = onToggleAllTerms,
        )

        Spacer(Modifier.height(20.dp))

        Spacer(
            Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SixpackTheme.colors.gray50),
        )

        Spacer(Modifier.height(20.dp))

        TermType.entries.forEach { term ->
            TermRow(
                term = term,
                isChecked = termsState[term] ?: false,
                onClickToggle = { isChecked ->
                    onToggle(term, isChecked)
                },
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun AgreeAllTermsRow(
    isChecked: Boolean,
    onClickToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckToggle(
            onClick = onClickToggle,
            isChecked = isChecked,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.onboarding_permission_agree_all),
            style = SixpackTheme.typography.b1Bold,
        )
    }
}

@Composable
fun TermRow(
    term: TermType,
    isChecked: Boolean,
    onClickToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckToggle(
            onClick = onClickToggle,
            isChecked = isChecked,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = stringResource(term.title),
            style = SixpackTheme.typography.b1Regular,
        )

        Spacer(modifier = Modifier.weight(1f))

        TermDetailButton(onClick = {})
    }
}

@Composable
private fun CheckToggle(
    onClick: (Boolean) -> Unit,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
) {
    val checked =
        if (isChecked) {
            R.drawable.ill_session_completed
        } else {
            R.drawable.ill_session_uncompleted
        }
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(32.dp)
                .noRippleClickable(onClick = { onClick(!isChecked) }),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(checked),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun TermDetailButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(32.dp)
                .noRippleClickable(onClick = onClick),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = SixpackTheme.colors.gray200,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPermissionScreenPreview() {
    SixpackTheme {
        Surface(color = Color.White) {
            OnboardingPermissionScreen(
                uiState = remember { mutableStateOf(OnboardingPermissionUiState()) },
                onToggleAllTerms = {},
                onToggleTerm = { _, _ -> },
                onClickNextButton = { /* TODO: 다음 버튼 클릭됨 */ },
                onClickBackButton = { /* TODO: 뒤로가기 버튼 클릭됨 */ },
            )
        }
    }
}
