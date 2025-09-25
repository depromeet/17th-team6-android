package com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.goal

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

enum class GoalType(
    val runningGoal: String,
    @StringRes val title: Int,
    @StringRes val subTitle: Int,
    @DrawableRes val img: Int
) {
    MARATHON(
        RunningGoalConstants.MARATHON,
        R.string.onboarding_goal_marathon_title,
        R.string.onboarding_goal_marathon_sub_title,
        R.drawable.ill_marathon
    ),
    STAMINA(
        RunningGoalConstants.STAMINA,
        R.string.onboarding_goal_stamina_title,
        R.string.onboarding_goal_stamina_sub_title,
        R.drawable.ill_marathon
    ),
    ZONE_2(
        RunningGoalConstants.ZONE_2,
        R.string.onboarding_goal_zone_title,
        R.string.onboarding_goal_zone2_sub_title,
        R.drawable.ill_endurance
    ),
}
