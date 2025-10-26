package com.dpm.sixpack.presentation.routes.signup.ui.component.terms.model

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

// TODO SR-N url 변경
private const val DEFAULT_TERM_URL = "https://depromeet.notion.site/29645b4338b380658ea4d47294188129?pvs=74"

enum class TermType(
    @StringRes val title: Int,
    val isRequired: Boolean,
    @StringRes val description: Int? = null,
    val url: String = DEFAULT_TERM_URL,
) {
    LOCATION(
        R.string.onboarding_permission_term_location,
        true,
        url = DEFAULT_TERM_URL,
    ),
    PRIVACY(
        R.string.onboarding_permission_term_privacy,
        true,
        url = DEFAULT_TERM_URL,
    ),
    MARKETING(
        R.string.onboarding_permission_term_marketing,
        false,
        url = DEFAULT_TERM_URL,
    ),
}
