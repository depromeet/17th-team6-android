package com.dpm.sixpack.presentation.routes.signup.contract

import android.os.Parcelable
import com.dpm.sixpack.presentation.common.base.UiState
import com.dpm.sixpack.presentation.common.model.NetworkErrorType
import com.dpm.sixpack.presentation.common.model.PhoneAuthStep
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpState(
    val step: PhoneAuthStep = PhoneAuthStep.PHONE_INPUT,
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val remainingTimeInSeconds: Int = 180, // 3 minutes
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val networkError: NetworkErrorType? = null,
    val showRegisteredDialog: Boolean = false,
    val registeredPhoneNumber: String = "",
) : UiState,
    Parcelable {
    val isPhoneNumberValid: Boolean
        get() = phoneNumber.all { it.isDigit() } && phoneNumber.length == 11

    val isVerificationCodeValid: Boolean
        get() = verificationCode.length == 6 && verificationCode.all { it.isDigit() }

    val isNextButtonEnabled: Boolean
        get() =
            when (step) {
                PhoneAuthStep.PHONE_INPUT -> isPhoneNumberValid && !isLoading
                PhoneAuthStep.VERIFICATION_INPUT -> isVerificationCodeValid && !isLoading
            }

    val formattedRemainingTime: String
        get() {
            val minutes = remainingTimeInSeconds / 60
            val seconds = remainingTimeInSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }

    companion object {
        const val RETRY_TIME_IN_SECONDS = 180
    }
}
