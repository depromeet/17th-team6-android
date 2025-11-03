package com.dpm.sixpack.presentation.routes.signin.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface SignInIntent : UiIntent {
    /**
     * 전화번호 입력 필드 변경 시
     */
    data class OnPhoneNumberChanged(
        val phoneNumber: String,
    ) : SignInIntent

    /**
     * 인증번호 입력 필드 변경 시
     */
    data class OnVerificationCodeChanged(
        val code: String,
    ) : SignInIntent

    /**
     * 인증번호 발송 버튼 클릭 시
     */
    data object OnSendVerificationCodeClick : SignInIntent

    /**
     * 인증번호 확인 버튼 클릭 시
     */
    data object OnVerifyCodeClick : SignInIntent

    /**
     * 인증번호 재발송 버튼 클릭 시
     */
    data object OnResendCodeClick : SignInIntent

    /**
     * 뒤로가기 버튼 클릭 시
     */
    data object OnBackButtonClick : SignInIntent

    /**
     * 미가입 사용자 다이얼로그 닫기
     */
    data object OnDismissUnregisteredDialog : SignInIntent

    /**
     * 회원가입 버튼 클릭 시 (미가입 사용자 다이얼로그에서)
     */
    data class OnSignUpClick(
        val phoneNumber: String,
    ) : SignInIntent

    /**
     * 계정찾기 버튼 클릭 시
     */
    data object OnFindAccountClick : SignInIntent
}
