package com.dpm.sixpack.presentation.routes.settings.accountinfo

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.usecase.user.GetUserProfileUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoIntent
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoSideEffect
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getUserProfileUseCase: GetUserProfileUseCase,
    ) : BaseViewModel<AccountInfoState, AccountInfoIntent, AccountInfoSideEffect>() {
        override val initialState: AccountInfoState = AccountInfoState()

        override val container: Container<AccountInfoState, AccountInfoSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            loadUserProfile()
        }

        override fun onIntent(intent: AccountInfoIntent) {
            when (intent) {
                AccountInfoIntent.OnBackButtonClick -> handleBackButtonClick()
            }
        }

        private fun handleBackButtonClick() =
            intent {
                postSideEffect(AccountInfoSideEffect.NavigateBack)
            }

        /**
         * 사용자 프로필 정보 로드
         */
        private fun loadUserProfile() =
            intent {
                reduce { state.copy(isLoading = true) }

                getUserProfileUseCase()
                    .onSuccess { userProfile ->
                        // createdAt 포맷 변환 (ISO 8601 -> yyyy.MM.dd)
                        val formattedDate =
                            try {
                                // "2025-04-25T10:30:00" -> "2025.04.25"
                                userProfile.createdAt.substringBefore("T").replace("-", ".")
                            } catch (e: Exception) {
                                Timber.e(e, "Failed to format date")
                                userProfile.createdAt
                            }

                        reduce {
                            state.copy(
                                isLoading = false,
                                userId = userProfile.id,
                                nickname = userProfile.nickname,
                                profileImageUrl = userProfile.profileImageUrl,
                                code = userProfile.code,
                                phoneNumber = userProfile.phoneNumberFormatted,
                                joinDate = formattedDate,
                                errorMessage = null,
                            )
                        }
                    }.onError { exception ->
                        Timber.e(exception, "Failed to load user profile")
                        reduce {
                            state.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "프로필 정보를 불러오는데 실패했습니다.",
                            )
                        }
                    }
            }
    }
