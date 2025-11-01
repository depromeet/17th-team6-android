package com.dpm.sixpack.presentation.routes.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.usecase.SendSmsCodeUseCase
import com.dpm.sixpack.domain.usecase.VerifySmsCodeUseCase
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
    private val sendSmsCodeUseCase: SendSmsCodeUseCase,
    private val verifySmsCodeUseCase: VerifySmsCodeUseCase,
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
                postSideEffect(SignUpSideEffect.ShowInvalidPhoneNumberError)
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            sendSmsCodeUseCase(state.phoneNumber)
                .onSuccess {
                    reduce {
                        state.copy(
                            step = SignUpStep.VERIFICATION_INPUT,
                            isLoading = false,
                            remainingTimeInSeconds = 180,
                        )
                    }

                    startTimer()
                    postSideEffect(SignUpSideEffect.ShowCodeSentSuccess)
                    Timber.d("Verification code sent to ${state.phoneNumber}")
                }.onError { exception ->
                    Timber.e("Failed to send verification code: ${exception.message}")
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message,
                        )
                    }
                    postSideEffect(SignUpSideEffect.ShowCodeSendFailedError)
                }
        }

    private fun handleVerifyCode() =
        intent {
            if (!state.isVerificationCodeValid) {
                postSideEffect(SignUpSideEffect.ShowInvalidCodeLengthError)
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            val result =
                verifySmsCodeUseCase(
                    phoneNumber = state.phoneNumber,
                    verificationCode = state.verificationCode,
                )

            stopTimer()

            result
                .onSuccess { verificationResult ->
                    reduce { state.copy(isLoading = false) }

                    if (verificationResult.isExistingUser) {
                        // User already registered, navigate to sign in
                        postSideEffect(
                            SignUpSideEffect.ShowAlreadyRegisteredUserDialog(state.phoneNumber),
                        )
                        Timber.d("User already registered: ${state.phoneNumber}")
                    } else {
                        // New user, proceed to profile creation
                        postSideEffect(SignUpSideEffect.NavigateToProfileCreation)
                        Timber.d("Phone number verified successfully, moving to profile creation")
                    }
                }.onError { exception ->
                    Timber.e("Failed to verify phone number: ${exception.message}")
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message,
                        )
                    }
                    postSideEffect(SignUpSideEffect.ShowCodeMismatchError)
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
                        state.copy(errorMessage = null)
                    }
                    postSideEffect(SignUpSideEffect.ShowCodeExpiredError)
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
