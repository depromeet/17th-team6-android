package com.dpm.sixpack.presentation.routes.friendprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.usecase.GetUserSummaryUseCase
import com.dpm.sixpack.domain.util.DoRunResult
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.common.model.NetworkErrorType
import com.dpm.sixpack.presentation.destinations.FriendProfile
import com.dpm.sixpack.presentation.routes.friendprofile.contract.FriendProfileIntent
import com.dpm.sixpack.presentation.routes.friendprofile.contract.FriendProfileSideEffect
import com.dpm.sixpack.presentation.routes.friendprofile.contract.FriendProfileState
import com.dpm.sixpack.presentation.routes.mypage.contract.ProfileInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FriendProfileViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getUserSummaryUseCase: GetUserSummaryUseCase,
    ) : BaseViewModel<FriendProfileState, FriendProfileIntent, FriendProfileSideEffect>() {
        override val initialState: FriendProfileState = FriendProfileState()

        override val container: Container<FriendProfileState, FriendProfileSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        private val userId: Long = savedStateHandle.toRoute<FriendProfile>().friendId

        init {
            loadUserProfile()
        }

        private fun loadUserProfile() {
            viewModelScope.launch {
                intent {
                    reduce { state.copy(isLoading = true) }
                }

                when (val result = getUserSummaryUseCase(userId)) {
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
                        Timber.d("SR-N ${result.exception}")
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

        override fun onIntent(intent: FriendProfileIntent) {
            when (intent) {
                is FriendProfileIntent.OnBackClick -> handleBackClick()
                is FriendProfileIntent.OnPostClick -> handlePostClick(intent.postId)
                is FriendProfileIntent.OnRetryClick -> handleRetryClick()
            }
        }

        private fun handleBackClick() =
            intent {
                postSideEffect(FriendProfileSideEffect.NavigateBack)
            }

        private fun handlePostClick(postId: Long) =
            intent {
                postSideEffect(FriendProfileSideEffect.NavigateToPostDetail(postId))
            }

        private fun handleRetryClick() {
            loadUserProfile()
        }
    }
