package com.dpm.sixpack.presentation.routes.signup.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpState(
    val step: SignUpStep = SignUpStep.PHONE_INPUT,
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val remainingTimeInSeconds: Int = 180, // 3 minutes
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState,
    Parcelable {
    val isPhoneNumberValid: Boolean
        get() = phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() }

    val isVerificationCodeValid: Boolean
        get() = verificationCode.length == 6 && verificationCode.all { it.isDigit() }

    val isNextButtonEnabled: Boolean
        get() =
            when (step) {
                SignUpStep.PHONE_INPUT -> isPhoneNumberValid && !isLoading
                SignUpStep.VERIFICATION_INPUT -> isVerificationCodeValid && !isLoading
            }

    val formattedRemainingTime: String
        get() {
            val minutes = remainingTimeInSeconds / 60
            val seconds = remainingTimeInSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
}

enum class SignUpStep {
    PHONE_INPUT,
    VERIFICATION_INPUT,
}
