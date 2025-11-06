package com.dpm.sixpack.presentation.routes.mypage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.usecase.GetUserSummaryUseCase
import com.dpm.sixpack.domain.util.DoRunResult
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageIntent
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageSideEffect
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageState
import com.dpm.sixpack.presentation.routes.mypage.contract.MyPageTab
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getUserSummaryUseCase: GetUserSummaryUseCase,
    ) : BaseViewModel<MyPageState, MyPageIntent, MyPageSideEffect>() {
        override val initialState: MyPageState = MyPageState()

        override val container: Container<MyPageState, MyPageSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            loadUserProfile()
        }

        private fun loadUserProfile() {
            viewModelScope.launch {
                when (val result = getUserSummaryUseCase()) {
                    is DoRunResult.Success -> {
                        val userSummary = result.data
                        intent {
                            reduce {
                                state.copy(
                                    profileInfo =
                                        ProfileInfo(
                                            nickname = userSummary.name,
                                            profileImageUrl = userSummary.imageUrl,
                                            friendCount = userSummary.friendCount,
                                            totalDistanceKm = userSummary.totalDistance / 1000.0,
                                            certificationCount = userSummary.selfieCount,
                                        ),
                                )
                            }
                        }
                    }
                    is DoRunResult.Failure -> {
                        // Handle error - keep default ProfileInfo
                        // Could show error message via SideEffect if needed
                    }
                }
            }
        }

        override fun onIntent(intent: MyPageIntent) {
            when (intent) {
                is MyPageIntent.OnTabClick -> handleTabClick(intent.tab)
                is MyPageIntent.OnSettingClick -> handleSettingClick()
            }
        }

        private fun handleTabClick(tab: MyPageTab) =
            intent {
                reduce {
                    state.copy(selectedTab = tab)
                }
            }

        private fun handleSettingClick() =
            intent {
                postSideEffect(MyPageSideEffect.NavigateToSettings)
            }
    }
