package com.dpm.sixpack.presentation.routes.session.contract.uistate

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

enum class RunningScreenTabItem(
    @StringRes val title: Int,
) {
    GOAL(R.string.screen_tab_goal_title),
    MAP(R.string.screen_tab_map_title),
}
