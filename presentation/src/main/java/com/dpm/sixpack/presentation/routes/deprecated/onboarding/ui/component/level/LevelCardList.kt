package com.dpm.sixpack.presentation.routes.deprecated.onboarding.ui.component.level

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.deprecated.onboarding.contract.uistate.level.LevelType

@Composable
fun LevelCardList(
    modifier: Modifier = Modifier,
    selectedLevel: LevelType? = null,
    onSelectLevel: (LevelType) -> Unit = {},
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

@Preview
@Composable
private fun LevelCardListPreview1() {
    DoRunPreviewWrapper {
        Column(modifier = Modifier) {
            LevelCardList(
                selectedLevel = LevelType.BEGINNER,
            )
        }
    }
}

@Preview
@Composable
private fun LevelCardListPreview2() {
    DoRunPreviewWrapper {
        Column(modifier = Modifier) {
            LevelCardList()
        }
    }
}
