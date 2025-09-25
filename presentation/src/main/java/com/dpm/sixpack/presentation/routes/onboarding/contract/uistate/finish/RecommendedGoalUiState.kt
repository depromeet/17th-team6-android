package com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.finish

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendedGoalUiState(
     val title : String,
     val subTitle: String,
    val goalTarget : GoalUiState,
    val isRecommended : Boolean,
    val isSelected : Boolean
): Parcelable
