package com.dpm.sixpack.presentation.routes.onboarding.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dpm.sixpack.presentation.R

enum class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int
) {
    Page1(
        R.string.onboarding_title_1,
        R.string.onboarding_desc_1,
        R.drawable.ill_character_success // TODO SR-N change image
    ),
    Page2(
        R.string.onboarding_title_2,
        R.string.onboarding_desc_2,
        R.drawable.ill_character_success // TODO TODO SR-N change image
    ),
    Page3(
        R.string.onboarding_title_3,
        R.string.onboarding_desc_3,
        R.drawable.ill_character_success // TODO TODO SR-N change image
    )
}
