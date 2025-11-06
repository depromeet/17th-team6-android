package com.dpm.sixpack.presentation.routes.settings.accountinfo

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoIntent
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoSideEffect
import com.dpm.sixpack.presentation.routes.settings.accountinfo.contract.AccountInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        // TODO: GetUserInfoUseCase 주입
    ) : BaseViewModel<AccountInfoState, AccountInfoIntent, AccountInfoSideEffect>() {
        override val initialState: AccountInfoState =
            AccountInfoState(
                // TODO: 실제 사용자 정보로 초기화
                phoneNumber = "010-7724-8020",
                joinDate = "2025.04.25",
            )

        override val container: Container<AccountInfoState, AccountInfoSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            // TODO: 실제 사용자 정보 로드
            // loadUserInfo()
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

//    private fun loadUserInfo() =
//        intent {
//            reduce { state.copy(isLoading = true) }
//
//            getUserInfoUseCase()
//                .onSuccess { userInfo ->
//                    reduce {
//                        state.copy(
//                            isLoading = false,
//                            phoneNumber = userInfo.phoneNumber,
//                            joinDate = userInfo.joinDate,
//                        )
//                    }
//                }
//                .onError { exception ->
//                    reduce { state.copy(isLoading = false) }
//                }
//        }
    }
