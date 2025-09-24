package com.dpm.sixpack.presentation.routes.onboarding.permission.contract.uistate

import androidx.annotation.DrawableRes
import com.dpm.sixpack.presentation.R

enum class TermType(@DrawableRes val title: Int, val isRequired: Boolean, @DrawableRes val description: Int? = null) {
    LOCATION(R.string.onboarding_permission_term_location, true),
    PRIVACY(R.string.onboarding_permission_term_privacy, true),
    MARKETING(R.string.onboarding_permission_term_marketing,false),
}
