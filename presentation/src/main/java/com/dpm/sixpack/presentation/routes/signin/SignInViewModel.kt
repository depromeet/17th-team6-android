package com.dpm.sixpack.presentation.routes.signin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.usecase.SendSmsCodeUseCase
import com.dpm.sixpack.domain.usecase.VerifySmsCodeUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.PhoneAuthStep
import com.dpm.sixpack.presentation.routes.signin.contract.SignInIntent
import com.dpm.sixpack.presentation.routes.signin.contract.SignInSideEffect
import com.dpm.sixpack.presentation.routes.signin.contract.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sendSmsCodeUseCase: SendSmsCodeUseCase,
    private val verifySmsCodeUseCase: VerifySmsCodeUseCase,
) : BaseViewModel<SignInState, SignInIntent, SignInSideEffect>() {
    override val initialState: SignInState = SignInState()

    override val container: Container<SignInState, SignInSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    private var timerJob: Job? = null

    override fun onIntent(intent: SignInIntent) {
        when (intent) {
            is SignInIntent.OnPhoneNumberChanged -> handlePhoneNumberChanged(intent.phoneNumber)
            is SignInIntent.OnVerificationCodeChanged -> handleVerificationCodeChanged(intent.code)
            is SignInIntent.OnSendVerificationCodeClick -> handleSendVerificationCode()
            is SignInIntent.OnVerifyCodeClick -> handleVerifyCode()
            is SignInIntent.OnResendCodeClick -> handleResendCode()
            is SignInIntent.OnBackButtonClick -> handleBackButtonClick()
            is SignInIntent.OnSignUpClick -> handleSignUpClick(intent.phoneNumber)
            is SignInIntent.OnDismissUnregisteredDialog -> handleDismissUnregisteredDialog()
        }
    }

    private fun handlePhoneNumberChanged(phoneNumber: String) =
        intent {
            val digitsOnly = phoneNumber.filter { it.isDigit() }.take(11)
            reduce {
                state.copy(
                    phoneNumber = digitsOnly,
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
                postSideEffect(SignInSideEffect.ShowInvalidPhoneNumberError)
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            sendSmsCodeUseCase(state.phoneNumber)
                .onSuccess {
                    reduce {
                        state.copy(
                            step = PhoneAuthStep.VERIFICATION_INPUT,
                            isLoading = false,
                            remainingTimeInSeconds = 180,
                        )
                    }

                    startTimer()
                    postSideEffect(SignInSideEffect.ShowCodeSentSuccess)
                    Timber.d("Verification code sent to ${state.phoneNumber}")
                }.onError { exception ->
                    Timber.e("Failed to send verification code: ${exception.message}")
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message,
                        )
                    }
                    postSideEffect(SignInSideEffect.ShowCodeSendFailedError)
                }
        }

    private fun handleVerifyCode() =
        intent {
            if (!state.isVerificationCodeValid) {
                postSideEffect(SignInSideEffect.ShowInvalidCodeLengthError)
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
                    if (verificationResult.isExistingUser) {
                        // Existing user, proceed to login
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(SignInSideEffect.NavigateToHome)
                        Timber.d("Sign in verified, navigating to home")
                    } else {
                        // User not registered, show dialog
                        reduce {
                            state.copy(
                                isLoading = false,
                                showUnregisteredDialog = true,
                                unregisteredPhoneNumber = state.phoneNumber,
                            )
                        }
                        Timber.d("User not registered: ${state.phoneNumber}")
                    }
                }.onError { exception ->
                    Timber.e("Failed to verify phone number: ${exception.message}")
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message,
                        )
                    }
                    postSideEffect(SignInSideEffect.ShowCodeMismatchError)
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
                PhoneAuthStep.PHONE_INPUT -> {
                    postSideEffect(SignInSideEffect.NavigateBack)
                }

                PhoneAuthStep.VERIFICATION_INPUT -> {
                    stopTimer()
                    reduce {
                        state.copy(
                            step = PhoneAuthStep.PHONE_INPUT,
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
                    postSideEffect(SignInSideEffect.ShowCodeExpiredError)
                }
            }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun handleSignUpClick(phoneNumber: String) {
        intent {
            postSideEffect(SignInSideEffect.NavigateToSignUp(phoneNumber))
        }
    }

    private fun handleDismissUnregisteredDialog() =
        intent {
            reduce {
                state.copy(
                    showUnregisteredDialog = false,
                    unregisteredPhoneNumber = "",
                )
            }
        }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
