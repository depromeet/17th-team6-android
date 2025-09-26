package com.dpm.sixpack.presentation.common.components.goal.model.state

import android.os.Parcelable
import com.dpm.sixpack.domain.model.RecommendedGoal
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendedGoalUiState(
    val title: String,
    val subTitle: String,
    val goalTarget: GoalUiState,
    val isRecommended: Boolean,
    val isSelected: Boolean = false,
) : Parcelable {
    // TODO 임시 필드, ViewModel 에서 리스트에서 아이템 찾아내기 위해서 사용
    @IgnoredOnParcel
    val id: String = title + subTitle + goalTarget.toString()
}

fun RecommendedGoal.asUiState(index: Int) =
    RecommendedGoalUiState(
        title = title,
        subTitle = subTitle,
        isRecommended = index == 0,
        isSelected = false,
        goalTarget =
            GoalUiState(
                pace = goal.pace,
                distance = goal.distance,
                duration = goal.duration,
                roundCount = goal.roundCount,
            ),
    )
