package com.dpm.sixpack.presentation.routes.settings

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.core.configs.BuildConfigProvider
import com.dpm.sixpack.domain.usecase.LogoutUseCase
import com.dpm.sixpack.domain.usecase.WithdrawUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.util.constant.Url
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
        private val logoutUseCase: LogoutUseCase,
        private val withdrawUseCase: WithdrawUseCase,
        private val buildConfigProvider: BuildConfigProvider,
    ) : BaseViewModel<SettingsState, SettingsIntent, SettingsSideEffect>() {
        override val initialState: SettingsState =
            SettingsState(
                appVersion = buildConfigProvider.getAppVersion(),
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
                SettingsIntent.OnDismissLogoutDialog -> handleDismissLogoutDialog()
                SettingsIntent.OnDismissWithdrawDialog -> handleDismissWithdrawDialog()
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
                postSideEffect(SettingsSideEffect.NavigateToExternalUrl(Url.DEFAULT_TERM_URL))
            }

        private fun handleTermsClick() =
            intent {
                // TODO: 실제 약관 및 정책 URL로 변경
                postSideEffect(SettingsSideEffect.NavigateToExternalUrl(Url.DEFAULT_TERM_URL))
            }

        private fun handleLogoutClick() =
            intent {
                reduce { state.copy(showLogoutDialog = true) }
            }

        private fun handleWithdrawClick() =
            intent {
                reduce { state.copy(showWithdrawDialog = true) }
            }

        private fun handleDismissLogoutDialog() =
            intent {
                reduce { state.copy(showLogoutDialog = false) }
            }

        private fun handleDismissWithdrawDialog() =
            intent {
                reduce { state.copy(showWithdrawDialog = false) }
            }

        private fun handleLogoutConfirm() =
            intent {
                reduce { state.copy(isLoading = true) }

                logoutUseCase()
                    .onSuccess {
                        reduce {
                            state.copy(
                                isLoading = false,
                                showLogoutDialog = false,
                            )
                        }
                        postSideEffect(SettingsSideEffect.LogoutSuccess)
                    }.onError {
                        reduce {
                            state.copy(
                                isLoading = false,
                                showLogoutDialog = false,
                            )
                        }
                        postSideEffect(SettingsSideEffect.LogoutFailed)
                    }
            }

        private fun handleWithdrawConfirm() =
            intent {
                reduce { state.copy(isLoading = true) }

                withdrawUseCase()
                    .onSuccess {
                        reduce {
                            state.copy(
                                isLoading = false,
                                showWithdrawDialog = false,
                            )
                        }
                        postSideEffect(SettingsSideEffect.WithdrawSuccess)
                    }.onError {
                        reduce {
                            state.copy(
                                isLoading = false,
                                showWithdrawDialog = false,
                            )
                        }
                        postSideEffect(SettingsSideEffect.WithdrawFailed)
                    }
            }
    }
