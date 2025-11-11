package com.dpm.sixpack.presentation.routes.signup.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface SignUpIntent : UiIntent {
    /**
     * 전화번호 입력 필드 변경 시
     */
    data class OnPhoneNumberChanged(
        val phoneNumber: String,
    ) : SignUpIntent

    /**
     * 인증번호 입력 필드 변경 시
     */
    data class OnVerificationCodeChanged(
        val code: String,
    ) : SignUpIntent

    /**
     * 인증번호 발송 버튼 클릭 시
     */
    data object OnSendVerificationCodeClick : SignUpIntent

    /**
     * 인증번호 확인 버튼 클릭 시
     */
    data object OnVerifyCodeClick : SignUpIntent

    /**
     * 인증번호 재발송 버튼 클릭 시
     */
    data object OnResendCodeClick : SignUpIntent

    /**
     * 뒤로가기 버튼 클릭 시
     */
    data object OnBackButtonClick : SignUpIntent

    /**
     * 계정찾기 버튼 클릭 시
     */
    data object OnFindAccountClick : SignUpIntent

    /**
     * 이미 가입된 사용자 다이얼로그 닫기
     */
    data object OnDismissRegisteredDialog : SignUpIntent

    /**
     * 네트워크 에러 다이얼로그 닫기
     */
    data object OnErrorDialogDismiss : SignUpIntent

    /**
     * 네트워크 에러 재시도 버튼 클릭 시
     */
    data object OnErrorRetry : SignUpIntent
}
