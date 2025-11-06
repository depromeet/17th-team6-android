package com.dpm.sixpack.presentation.routes.settings

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsIntent
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsSideEffect
import com.dpm.sixpack.presentation.routes.settings.contract.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val logoutUseCase: com.dpm.sixpack.domain.usecase.LogoutUseCase,
        private val withdrawUseCase: com.dpm.sixpack.domain.usecase.WithdrawUseCase,
    ) : BaseViewModel<SettingsState, SettingsIntent, SettingsSideEffect>() {
        override val initialState: SettingsState =
            SettingsState(
                appVersion = "3.13.0", // TODO: 실제 버전 정보로 변경
            )

        override val container: Container<SettingsState, SettingsSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        override fun onIntent(intent: SettingsIntent) {
            when (intent) {
                SettingsIntent.OnBackButtonClick -> handleBackButtonClick()
                SettingsIntent.OnProfileEditClick -> handleProfileEditClick()
                SettingsIntent.OnAccountInfoClick -> handleAccountInfoClick()
                SettingsIntent.OnPushNotificationClick -> handlePushNotificationClick()
                SettingsIntent.OnPrivacyPolicyClick -> handlePrivacyPolicyClick()
                SettingsIntent.OnTermsClick -> handleTermsClick()
                SettingsIntent.OnLogoutClick -> handleLogoutClick()
                SettingsIntent.OnWithdrawClick -> handleWithdrawClick()
                SettingsIntent.OnLogoutConfirm -> handleLogoutConfirm()
                SettingsIntent.OnWithdrawConfirm -> handleWithdrawConfirm()
            }
        }

        private fun handleBackButtonClick() =
            intent {
                postSideEffect(SettingsSideEffect.NavigateBack)
            }

        private fun handleProfileEditClick() =
            intent {
                postSideEffect(SettingsSideEffect.NavigateToProfileEdit)
            }

        private fun handleAccountInfoClick() =
            intent {
                postSideEffect(SettingsSideEffect.NavigateToAccountInfo)
            }

        private fun handlePushNotificationClick() =
            intent {
                postSideEffect(SettingsSideEffect.NavigateToPushNotification)
            }

        private fun handlePrivacyPolicyClick() =
            intent {
                // TODO: 실제 개인정보처리방침 URL로 변경
                postSideEffect(SettingsSideEffect.NavigateToExternalUrl("https://www.dorun.com/privacy"))
            }

        private fun handleTermsClick() =
            intent {
                // TODO: 실제 약관 및 정책 URL로 변경
                postSideEffect(SettingsSideEffect.NavigateToExternalUrl("https://www.dorun.com/terms"))
            }

        private fun handleLogoutClick() =
            intent {
                postSideEffect(SettingsSideEffect.ShowLogoutDialog)
            }

        private fun handleWithdrawClick() =
            intent {
                postSideEffect(SettingsSideEffect.ShowWithdrawDialog)
            }

        private fun handleLogoutConfirm() =
            intent {
                reduce { state.copy(isLoading = true) }

                logoutUseCase()
                    .onSuccess {
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(SettingsSideEffect.LogoutSuccess)
                    }.onError {
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(SettingsSideEffect.LogoutFailed)
                    }
            }

        private fun handleWithdrawConfirm() =
            intent {
                reduce { state.copy(isLoading = true) }

                withdrawUseCase()
                    .onSuccess {
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(SettingsSideEffect.WithdrawSuccess)
                    }.onError {
                        reduce { state.copy(isLoading = false) }
                        postSideEffect(SettingsSideEffect.WithdrawFailed)
                    }
            }
    }
