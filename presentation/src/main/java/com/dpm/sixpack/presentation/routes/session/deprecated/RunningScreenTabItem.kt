package com.dpm.sixpack.presentation.routes.session.deprecated

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

enum class RunningScreenTabItem(
    @StringRes val title: Int,
) {
    GOAL(R.string.screen_tab_goal_title),
    MAP(R.string.screen_tab_map_title),
}
