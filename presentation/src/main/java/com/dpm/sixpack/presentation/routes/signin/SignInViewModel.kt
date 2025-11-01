package com.dpm.sixpack.presentation.routes.signin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.signin.contract.SignInIntent
import com.dpm.sixpack.presentation.routes.signin.contract.SignInSideEffect
import com.dpm.sixpack.presentation.routes.signin.contract.SignInState
import com.dpm.sixpack.presentation.routes.signin.contract.SignInStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

private typealias SignInSyntax = Syntax<SignInState, SignInSideEffect>

@HiltViewModel
class SignInViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    // TODO: Inject use cases
    // private val sendVerificationCodeUseCase: SendVerificationCodeUseCase,
    // private val verifyPhoneNumberUseCase: VerifyPhoneNumberUseCase,
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

            try {
                // TODO: Replace with actual API call
                // val result = sendVerificationCodeUseCase(state.phoneNumber)
                delay(1000) // Simulate API call

                reduce {
                    state.copy(
                        step = SignInStep.VERIFICATION_INPUT,
                        isLoading = false,
                        remainingTimeInSeconds = 180,
                    )
                }

                startTimer()
                postSideEffect(SignInSideEffect.ShowCodeSentSuccess)
                Timber.d("Verification code sent to ${state.phoneNumber}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to send verification code")
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = null,
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

            try {
                // TODO: Replace with actual API call
                // val result = verifyPhoneNumberUseCase(
                //     phoneNumber = state.phoneNumber,
                //     verificationCode = state.verificationCode
                // )
                delay(1000) // Simulate API call

                stopTimer()

                // Check user registration status
                // TODO: This should be determined by API response
                val isRegistered = false // Placeholder
                if (!isRegistered) {
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(
                        SignInSideEffect.ShowUnregisteredUserDialog(state.phoneNumber),
                    )
                } else {
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(SignInSideEffect.NavigateToHome)
                    Timber.d("Sign in verified, navigating to home")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to verify phone number")
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = null,
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
                SignInStep.PHONE_INPUT -> {
                    postSideEffect(SignInSideEffect.NavigateBack)
                }
                SignInStep.VERIFICATION_INPUT -> {
                    stopTimer()
                    reduce {
                        state.copy(
                            step = SignInStep.PHONE_INPUT,
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

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
