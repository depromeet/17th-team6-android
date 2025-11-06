package com.dpm.sixpack.presentation.routes.feed.certifiedusers.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface CertifiedUsersIntent : UiIntent {
    data object OnBackIconClick : CertifiedUsersIntent

    data class OnUserProfileClick(
        val userId: Long,
        val isMe: Boolean,
    ) : CertifiedUsersIntent
}
