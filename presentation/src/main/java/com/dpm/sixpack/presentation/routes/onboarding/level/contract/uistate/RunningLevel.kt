package com.dpm.sixpack.presentation.routes.onboarding.level.contract.uistate

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

enum class RunningLevel(
    val runningLevel: String,
    @StringRes val title: Int,
    @StringRes val subTitle: Int,
    @DrawableRes val img: Int
) {
    BEGINNER(
        runningLevel = RunningLevelConstants.BEGINNER,
        title = R.string.onboarding_level_beginner_title,
        subTitle = R.string.onboarding_level_beginner_sub_title,
        img = R.drawable.ill_level_beginner
    ),
    OCCASIONAL(
        runningLevel = RunningLevelConstants.OCCASIONAL,
        title = R.string.onboarding_level_occasional_title,
        subTitle = R.string.onboarding_level_occasional_sub_title, img = R.drawable.ill_level_occasional
    ),
    CONSISTENT(
        runningLevel = RunningLevelConstants.CONSISTENT,
        title = R.string.onboarding_level_consistent_title,
        subTitle = R.string.onboarding_level_consistent_sub_title,
        img = R.drawable.ill_level_consistent
    ),
}


