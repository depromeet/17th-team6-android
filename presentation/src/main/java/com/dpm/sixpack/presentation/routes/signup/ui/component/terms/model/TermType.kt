package com.dpm.sixpack.presentation.routes.signup.ui.component.terms.model

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

enum class TermType(
    @StringRes val title: Int,
    val isRequired: Boolean,
    @StringRes val description: Int? = null,
) {
    LOCATION(R.string.onboarding_permission_term_location, true),
    PRIVACY(R.string.onboarding_permission_term_privacy, true),
    MARKETING(R.string.onboarding_permission_term_marketing, false),
}
