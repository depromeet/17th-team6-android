package com.dpm.sixpack.presentation.routes.mypage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.usecase.GetUserSummaryUseCase
import com.dpm.sixpack.domain.util.DoRunResult
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.NetworkErrorType
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
                intent {
                    reduce { state.copy(isLoading = true) }
                }

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
                                    isLoading = false,
                                    error = null,
                                )
                            }
                        }
                    }
                    is DoRunResult.Failure -> {
                        val errorType = mapExceptionToErrorType(result.exception)
                        intent {
                            reduce {
                                state.copy(
                                    isLoading = false,
                                    error = errorType,
                                )
                            }
                        }
                    }
                }
            }
        }

        /**
         * DoRunException을 NetworkErrorType으로 매핑
         */
        private fun mapExceptionToErrorType(exception: DoRunException): NetworkErrorType =
            when (exception) {
                is DoRunException.NetworkError -> NetworkErrorType.NetworkConnection
                is DoRunException.ServerError -> {
                    when (exception.code) {
                        404 -> NetworkErrorType.NotFound
                        500 -> NetworkErrorType.ServerError
                        502 -> NetworkErrorType.BadGateway
                        else -> NetworkErrorType.ServerError
                    }
                }
                else ->
                    NetworkErrorType.Custom(
                        title = "오류가 발생했어요.",
                        description = exception.message ?: "알 수 없는 오류가 발생했습니다.",
                    )
            }

        override fun onIntent(intent: MyPageIntent) {
            when (intent) {
                is MyPageIntent.OnTabClick -> handleTabClick(intent.tab)
                is MyPageIntent.OnSettingClick -> handleSettingClick()
                is MyPageIntent.OnRetryClick -> handleRetryClick()
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

        private fun handleRetryClick() {
            loadUserProfile()
        }
    }
