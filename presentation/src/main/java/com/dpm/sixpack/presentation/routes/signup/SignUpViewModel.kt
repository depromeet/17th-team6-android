package com.dpm.sixpack.presentation.routes.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.usecase.SendSmsCodeUseCase
import com.dpm.sixpack.domain.usecase.VerifySmsCodeUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.NetworkErrorType
import com.dpm.sixpack.presentation.common.model.PhoneAuthStep
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpIntent
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpSideEffect
import com.dpm.sixpack.presentation.routes.signup.contract.SignUpState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

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
            is SignUpIntent.OnFindAccountClick -> handleOnFindAccountClick()
            is SignUpIntent.OnDismissRegisteredDialog -> handleDismissRegisteredDialog()
            is SignUpIntent.OnErrorDialogDismiss -> handleErrorDialogDismiss()
            is SignUpIntent.OnErrorRetry -> handleErrorRetry()
        }
    }

    private fun handlePhoneNumberChanged(phoneNumber: String) =
        intent {
            reduce {
                state.copy(
                    phoneNumber = phoneNumber.take(11),
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
                            step = PhoneAuthStep.VERIFICATION_INPUT,
                            isLoading = false,
                            remainingTimeInSeconds = SignUpState.RETRY_TIME_IN_SECONDS,
                        )
                    }

                    startTimer()
                    postSideEffect(SignUpSideEffect.ShowCodeSentSuccess)
                    Timber.d("Verification code sent to ${state.phoneNumber}")
                }.onError { exception ->
                    Timber.e("Failed to send verification code: ${exception.message}")

                    // 네트워크 관련 에러는 다이얼로그로 표시
                    if (isNetworkRelatedError(exception)) {
                        showNetworkErrorDialog(exception)
                        return@onError
                    }

                    // 비즈니스 에러는 기존 토스트로 처리
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message,
                        )
                    }

                    when (exception) {
                        is DoRunException.ValidationError -> {
                            postSideEffect(SignUpSideEffect.ShowInvalidPhoneNumberError)
                        }
                        is DoRunException.RateLimitError -> {
                            postSideEffect(SignUpSideEffect.ShowRateLimitError)
                        }
                        else -> {
                            postSideEffect(SignUpSideEffect.ShowCodeSendFailedError)
                        }
                    }
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


            result
                .onSuccess { verificationResult ->
                    stopTimer()
                    
                    reduce { state.copy(isLoading = false) }

                    // isExistingUser 필드로 기존/신규 사용자 구분
                    if (verificationResult.isExistingUser) {
                        // 기존 사용자: 이미 등록됨, 다이얼로그 표시
                        reduce {
                            state.copy(
                                showRegisteredDialog = true,
                                registeredPhoneNumber = state.phoneNumber,
                            )
                        }
                        Timber.d("User already registered: ${state.phoneNumber}")
                    } else {
                        // 신규 사용자: 프로필 생성으로 이동
                        postSideEffect(SignUpSideEffect.NavigateToProfileCreation(phoneNumber = state.phoneNumber))
                        Timber.d("Phone number verified successfully (new user), moving to profile creation")
                    }
                }.onError { exception ->
                    Timber.e("Failed to verify phone number: ${exception.message}")

                    // 네트워크 관련 에러는 다이얼로그로 표시
                    if (isNetworkRelatedError(exception)) {
                        showNetworkErrorDialog(exception)
                        return@onError
                    }

                    // 비즈니스 에러는 기존 토스트로 처리
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message,
                        )
                    }

                    when (exception) {
                        is DoRunException.CodeMismatchError -> {
                            postSideEffect(SignUpSideEffect.ShowCodeMismatchError)
                        }
                        is DoRunException.CodeExpiredError -> {
                            postSideEffect(SignUpSideEffect.ShowCodeExpiredError)
                        }
                        else -> {
                            postSideEffect(SignUpSideEffect.ShowCodeMismatchError)
                        }
                    }
                }
        }

    private fun handleResendCode() =
        intent {
            stopTimer()

            reduce {
                state.copy(
                    verificationCode = "",
                    remainingTimeInSeconds = SignUpState.RETRY_TIME_IN_SECONDS,
                    errorMessage = null,
                )
            }

            handleSendVerificationCode()
        }

    private fun handleBackButtonClick() =
        intent {
            when (state.step) {
                PhoneAuthStep.PHONE_INPUT -> {
                    postSideEffect(SignUpSideEffect.NavigateBack)
                }

                PhoneAuthStep.VERIFICATION_INPUT -> {
                    stopTimer()
                    reduce {
                        state.copy(
                            step = PhoneAuthStep.PHONE_INPUT,
                            verificationCode = "",
                            remainingTimeInSeconds = SignUpState.RETRY_TIME_IN_SECONDS,
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
                            // 음수 방지: 0 이하로 내려가지 않도록 보장
                            val newTime = (state.remainingTimeInSeconds - 1).coerceAtLeast(0)
                            state.copy(remainingTimeInSeconds = newTime)
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

    private fun handleOnFindAccountClick() =
        intent {
            postSideEffect(SignUpSideEffect.NavigateToFindAccount)
        }

    private fun handleDismissRegisteredDialog() =
        intent {
            reduce {
                state.copy(
                    showRegisteredDialog = false,
                    registeredPhoneNumber = "",
                )
            }
        }

    private fun handleErrorDialogDismiss() =
        intent {
            reduce {
                state.copy(networkError = null)
            }
        }

    private fun handleErrorRetry() =
        intent {
            // 에러 다이얼로그 닫기
            reduce {
                state.copy(networkError = null)
            }

            // 현재 단계에 맞는 재시도 로직 실행
            when (state.step) {
                PhoneAuthStep.PHONE_INPUT -> handleSendVerificationCode()
                PhoneAuthStep.VERIFICATION_INPUT -> handleVerifyCode()
            }
        }

    /**
     * DoRunException을 NetworkErrorType으로 매핑
     */
    private fun mapExceptionToErrorType(exception: DoRunException): NetworkErrorType {
        return when (exception) {
            is DoRunException.NetworkError -> NetworkErrorType.NetworkConnection
            is DoRunException.ServerError -> {
                when (exception.code) {
                    404 -> NetworkErrorType.NotFound
                    500 -> NetworkErrorType.ServerError
                    502 -> NetworkErrorType.BadGateway
                    else -> NetworkErrorType.ServerError
                }
            }
            else -> NetworkErrorType.Custom(
                title = "오류가 발생했어요.",
                description = exception.message ?: "알 수 없는 오류가 발생했습니다.",
            )
        }
    }

    /**
     * 네트워크 에러가 발생한 경우 다이얼로그 표시
     */
    private fun showNetworkErrorDialog(exception: DoRunException) =
        intent {
            val errorType = mapExceptionToErrorType(exception)
            reduce {
                state.copy(
                    isLoading = false,
                    networkError = errorType,
                )
            }
        }

    /**
     * 네트워크 관련 에러인지 확인
     */
    private fun isNetworkRelatedError(exception: DoRunException): Boolean =
        when (exception) {
            is DoRunException.NetworkError,
            is DoRunException.ServerError,
            -> true
            else -> false
        }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
