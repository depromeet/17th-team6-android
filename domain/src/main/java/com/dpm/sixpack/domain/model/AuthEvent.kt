package com.dpm.sixpack.domain.model

sealed class AuthEvent {

    data object LoggedOut : AuthEvent()

    data object TokenExpired : AuthEvent()

    data class TokenRefreshFailed(
        val reason: String,
    ) : AuthEvent()

    data object SessionInvalidated : AuthEvent()
}
