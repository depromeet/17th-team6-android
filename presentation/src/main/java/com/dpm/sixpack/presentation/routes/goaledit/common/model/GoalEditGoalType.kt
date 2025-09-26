package com.dpm.sixpack.presentation.routes.goaledit.common.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

enum class GoalEditGoalType(
    val id: String,
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
) {
    MARATHON(
        id = "marathon",
        icon = R.drawable.ill_marathon,
        title = R.string.home_goal_edit_for_marathon_title,
        description = R.string.home_goal_edit_for_marathon_description,
    ),
    STAMINA(
        id = "stamina",
        icon = R.drawable.ill_stamina,
        title = R.string.home_goal_edit_for_stamina_title,
        description = R.string.home_goal_edit_for_stamina_description,
    ),
    ENDURANCE(
        id = "endurance",
        icon = R.drawable.ill_endurance,
        title = R.string.home_goal_edit_for_endurance_title,
        description = R.string.home_goal_edit_for_endurance_description,
    ),
    ;

    companion object {
        fun fromId(id: String): GoalEditGoalType? = entries.find { it.id == id }
    }
}
