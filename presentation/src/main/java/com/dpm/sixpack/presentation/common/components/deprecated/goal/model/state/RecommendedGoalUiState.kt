package com.dpm.sixpack.presentation.common.components.deprecated.goal.model.state

import android.os.Parcelable
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
