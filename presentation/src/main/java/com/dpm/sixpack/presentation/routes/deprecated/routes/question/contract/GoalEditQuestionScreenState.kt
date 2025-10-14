package com.dpm.sixpack.presentation.routes.deprecated.routes.question.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.components.deprecated.goal.model.type.GoalType
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoalEditQuestionScreenState(
    val selectedGoalType: GoalType? = null,
) : UiState,
    Parcelable {
    @IgnoredOnParcel
    val enableNextButton: Boolean = selectedGoalType != null
}
