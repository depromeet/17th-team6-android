package com.dpm.sixpack.presentation.routes.signin.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignInState(
    val step: SignInStep = SignInStep.PHONE_INPUT,
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val remainingTimeInSeconds: Int = 180, // 3 minutes
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState,
    Parcelable {
    val isPhoneNumberValid: Boolean
        get() = phoneNumber.length >= 10 && phoneNumber.length <= 11

    val isVerificationCodeValid: Boolean
        get() = verificationCode.length == 6 && verificationCode.all { it.isDigit() }

    val isNextButtonEnabled: Boolean
        get() =
            when (step) {
                SignInStep.PHONE_INPUT -> isPhoneNumberValid && !isLoading
                SignInStep.VERIFICATION_INPUT -> isVerificationCodeValid && !isLoading
            }

    val formattedRemainingTime: String
        get() {
            val minutes = remainingTimeInSeconds / 60
            val seconds = remainingTimeInSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}

enum class SignInStep {
    PHONE_INPUT,
    VERIFICATION_INPUT,
}
