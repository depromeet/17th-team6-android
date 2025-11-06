package com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface CertifiedUsersSideEffect : SideEffect {
    data object NavigateBack : CertifiedUsersSideEffect

    data object NavigateToMyPage : CertifiedUsersSideEffect

    data class NavigateToUserPage(
        val userId: Long,
    ) : CertifiedUsersSideEffect
}
