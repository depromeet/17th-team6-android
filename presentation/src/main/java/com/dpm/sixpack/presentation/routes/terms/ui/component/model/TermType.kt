package com.dpm.sixpack.presentation.routes.terms.ui.component.model

import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.util.constant.Url.DEFAULT_TERM_URL

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
