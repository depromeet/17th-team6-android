package com.dpm.sixpack.presentation.routes.onboarding.contract.uistate.finish

import androidx.annotation.DrawableRes
import com.dpm.sixpack.presentation.R

enum class MarathonGoalType(val distance: Int, @DrawableRes val img: Int) {
    FULL(distance = 42195, img = R.drawable.ill_marathon_42km),
    HALF(distance = 21000, img = R.drawable.ill_marathon_21km),
    TEN(distance = 10000, img = R.drawable.ill_marathon_10km),
}
