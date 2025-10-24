package com.dpm.sixpack.presentation.routes.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpIntent
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpSideEffect
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpState
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

private typealias SignUpSyntax = Syntax<SignUpState, SignUpSideEffect>

@HiltViewModel
class SignUpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    // TODO: Inject use cases
    // private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    // private val verifyPhoneNumberUseCase: VerifyPhoneNumberUseCase,
) : BaseViewModel<SignUpState, SignUpIntent, SignUpSideEffect>() {
    override val initialState: SignUpState = SignUpState()

    override val container: Container<SignUpState, SignUpSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    private var timerJob: Job? = null

    override fun onIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.OnPhoneNumberChanged -> handlePhoneNumberChanged(intent.phoneNumber)
            is SignUpIntent.OnVerificationCodeChanged -> handleVerificationCodeChanged(intent.code)
            is SignUpIntent.OnSendVerificationCodeClick -> handleSendVerificationCode()
            is SignUpIntent.OnVerifyCodeClick -> handleVerifyCode()
            is SignUpIntent.OnResendCodeClick -> handleResendCode()
            is SignUpIntent.OnBackButtonClick -> handleBackButtonClick()
        }
    }

    private fun handlePhoneNumberChanged(phoneNumber: String) =
        intent {
            val filteredNumber = phoneNumber.filter { it.isDigit() }.take(11)
            reduce {
                state.copy(
                    phoneNumber = filteredNumber,
                    errorMessage = null,
                )
            }
        }

    private fun handleVerificationCodeChanged(code: String) =
        intent {
            val filteredCode = code.filter { it.isDigit() }.take(6)
            reduce {
                state.copy(
                    verificationCode = filteredCode,
                    errorMessage = null,
                )
            }
        }

    private fun handleSendVerificationCode() =
        intent {
            if (!state.isPhoneNumberValid) {
                postSideEffect(SignUpSideEffect.ShowToast("올바른 전화번호를 입력해주세요."))
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            try {
                // TODO: Replace with actual API call
                // val result = sendVerificationCodeUseCase(state.phoneNumber)
                delay(1000) // Simulate API call

                reduce {
                    state.copy(
                        step = SignUpStep.VERIFICATION_INPUT,
                        isLoading = false,
                        remainingTimeInSeconds = 180,
                    )
                }

                startTimer()
                postSideEffect(SignUpSideEffect.ShowToast("인증번호가 발송되었습니다."))
                Timber.d("Verification code sent to ${state.phoneNumber}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to send verification code")
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = "인증번호 발송에 실패했습니다.",
                    )
                }
                postSideEffect(SignUpSideEffect.ShowToast("인증번호 발송에 실패했습니다."))
            }
        }

    private fun handleVerifyCode() =
        intent {
            if (!state.isVerificationCodeValid) {
                postSideEffect(SignUpSideEffect.ShowToast("6자리 인증번호를 입력해주세요."))
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            try {
                // TODO: Replace with actual API call
                // val result = verifyPhoneNumberUseCase(
                //     phoneNumber = state.phoneNumber,
                //     verificationCode = state.verificationCode
                // )
                delay(1000) // Simulate API call

                stopTimer()
                reduce { state.copy(isLoading = false) }

                postSideEffect(SignUpSideEffect.NavigateToTermsAgreement)
                Timber.d("Phone number verified successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to verify phone number")
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = "인증번호가 일치하지 않습니다.",
                    )
                }
                postSideEffect(SignUpSideEffect.ShowToast("인증번호가 일치하지 않습니다."))
            }
        }

    private fun handleResendCode() =
        intent {
            stopTimer()

            reduce {
                state.copy(
                    verificationCode = "",
                    remainingTimeInSeconds = 180,
                    errorMessage = null,
                )
            }

            handleSendVerificationCode()
        }

    private fun handleBackButtonClick() =
        intent {
            when (state.step) {
                SignUpStep.PHONE_INPUT -> {
                    postSideEffect(SignUpSideEffect.NavigateBack)
                }
                SignUpStep.VERIFICATION_INPUT -> {
                    stopTimer()
                    reduce {
                        state.copy(
                            step = SignUpStep.PHONE_INPUT,
                            verificationCode = "",
                            remainingTimeInSeconds = 180,
                            errorMessage = null,
                        )
                    }
                }
            }
        }

    private fun startTimer() {
        stopTimer()
        timerJob =
            viewModelScope.launch {
                while (container.stateFlow.value.remainingTimeInSeconds > 0) {
                    delay(1000)
                    intent {
                        reduce {
                            state.copy(remainingTimeInSeconds = state.remainingTimeInSeconds - 1)
                        }
                    }
                }
                // Timer expired
                intent {
                    reduce {
                        state.copy(errorMessage = "인증 시간이 만료되었습니다. 다시 시도해주세요.")
                    }
                    postSideEffect(SignUpSideEffect.ShowToast("인증 시간이 만료되었습니다."))
                }
            }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
