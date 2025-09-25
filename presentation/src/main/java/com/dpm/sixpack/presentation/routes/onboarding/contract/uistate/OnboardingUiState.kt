package com.dpm.sixpack.presentation.routes.onboarding.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.finish.RecommendedGoalUiState
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.goal.GoalType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.level.LevelType
import com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.permission.TermType
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnboardingUiState(
    // Permission Screen State
    val termsState: Map<TermType, Boolean> = TermType.entries.associateWith { false },
    // Level Screen State
    val selectedLevel: LevelType? = null,
    // Goal Screen State
    val selectedGoal: GoalType? = null,
    val recommendedGoals: List<RecommendedGoalUiState> = emptyList(),
) : UiState,
    Parcelable {
    val isAllTermsChecked: Boolean
        get() = termsState.values.all { it }
    val isPermissionNextEnabled: Boolean
        get() = TermType.entries.filter { it.isRequired }.all { termsState[it] == true }
    val isLevelNextEnabled: Boolean
        get() = selectedLevel != null
    val isGoalNextEnabled: Boolean
        get() = selectedGoal != null

    val isFinishNextEnabled: Boolean
        get() = recommendedGoals.any { it.isSelected }
}
