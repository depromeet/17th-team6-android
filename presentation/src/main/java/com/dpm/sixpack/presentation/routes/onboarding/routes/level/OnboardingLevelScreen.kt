package com.dpm.sixpack.presentation.routes.onboarding.routes.level

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.OnboardingUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.LevelType
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun OnboardingLevelScreen(
    uiState: State<OnboardingUiState>,
    onSelectLevel: (LevelType) -> Unit,
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
            OnboardingPageIndicator(page = OnboardingPage.LEVEL)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.onboarding_level_title),
                style = SixpackTheme.typography.h2Bold,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))

            LevelCardList(
                selectedLevel = uiState.value.selectedLevel,
                onSelectLevel = onSelectLevel,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            )

            Spacer(Modifier.weight(1f))

            OnboardingNextButton(
                onClick = onClickNextButton,
                enabled = uiState.value.isLevelNextEnabled,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LevelCardList(
    selectedLevel: LevelType?,
    onSelectLevel: (LevelType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LevelType.entries.forEach { level ->
        LevelCard(
            level = level,
            isSelected = level == selectedLevel,
            onSelectLevel = onSelectLevel,
            modifier = modifier,
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LevelCard(
    level: LevelType,
    isSelected: Boolean,
    onSelectLevel: (LevelType) -> Unit,
    modifier: Modifier = Modifier,
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

@Preview(showBackground = true)
@Composable
private fun OnboardingLevelScreenPreview() {
    SixpackTheme {
        Surface(color = SixpackTheme.colors.gray0) {
            OnboardingLevelScreen(
                uiState = remember { mutableStateOf(OnboardingUiState()) },
                onSelectLevel = { selectedLevel ->
                },
                onClickNextButton = { },
                onClickBackButton = { },
            )
        }
    }
}
