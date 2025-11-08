package com.dpm.sixpack.presentation.routes.settings.profileedit

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.model.ProfileImageOption
import com.dpm.sixpack.domain.usecase.ConvertUriToFileUseCase
import com.dpm.sixpack.domain.usecase.user.GetUserProfileUseCase
import com.dpm.sixpack.domain.usecase.user.UpdateProfileUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.settings.profileedit.contract.ProfileEditIntent
import com.dpm.sixpack.presentation.routes.settings.profileedit.contract.ProfileEditSideEffect
import com.dpm.sixpack.presentation.routes.settings.profileedit.contract.ProfileEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getUserProfileUseCase: GetUserProfileUseCase,
        private val updateProfileUseCase: UpdateProfileUseCase,
        private val convertUriToFileUseCase: ConvertUriToFileUseCase,
    ) : BaseViewModel<ProfileEditState, ProfileEditIntent, ProfileEditSideEffect>() {
        override val initialState: ProfileEditState = ProfileEditState()

        override val container: Container<ProfileEditState, ProfileEditSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            loadUserProfile()
        }

        /**
         * 사용자 프로필 로드
         */
        private fun loadUserProfile() =
            intent {
                reduce { state.copy(isLoading = true) }

                getUserProfileUseCase()
                    .onSuccess { userProfile ->
                        reduce {
                            state.copy(
                                isLoading = false,
                                profileName = userProfile.nickname,
                                profileImageUri = userProfile.profileImageUrl,
                                originalProfileImageUrl = userProfile.profileImageUrl,
                            )
                        }
                    }.onError { exception ->
                        Timber.e(exception, "Failed to load user profile")
                        reduce {
                            state.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "프로필을 불러오는데 실패했습니다.",
                            )
                        }
                    }
            }

        override fun onIntent(intent: ProfileEditIntent) {
            when (intent) {
                ProfileEditIntent.OnBackButtonClick -> handleBackButtonClick()
                is ProfileEditIntent.OnProfileNameChanged -> handleProfileNameChanged(intent.name)
                ProfileEditIntent.OnPickImageClick -> handlePickImage()
                is ProfileEditIntent.OnProfileImageSelected -> handleProfileImageSelected(intent.imageUri)
                ProfileEditIntent.OnCompleteClick -> handleCompleteClick()
            }
        }

        private fun handleBackButtonClick() =
            intent {
                postSideEffect(ProfileEditSideEffect.NavigateBack)
            }

        private fun handleProfileNameChanged(name: String) =
            intent {
                reduce {
                    state.copy(
                        profileName = name.take(10), // 최대 10자 제한
                        errorMessage = null,
                    )
                }
            }

        private fun handlePickImage() =
            intent {
                postSideEffect(ProfileEditSideEffect.LaunchImagePicker)
            }

        private fun handleProfileImageSelected(imageUri: String) =
            intent {
                reduce {
                    state.copy(
                        profileImageUri = imageUri,
                        errorMessage = null,
                    )
                }
            }

        private fun handleCompleteClick() =
            intent {
                if (!state.isProfileNameValid) {
                    reduce {
                        state.copy(errorMessage = "닉네임은 2~10자로 입력해주세요.")
                    }
                    return@intent
                }

                reduce { state.copy(isLoading = true) }

                // Uri를 File로 변환
                val profileImageFile =
                    state.profileImageUri?.let { uriString ->
                        convertUriToFileUseCase(uriString).getOrNull()
                    }

                // imageOption 결정
                val imageOption =
                    when {
                        profileImageFile != null -> ProfileImageOption.SET
                        state.profileImageUri == null && state.originalProfileImageUrl != null -> ProfileImageOption.REMOVE
                        else -> ProfileImageOption.KEEP
                    }

                // 프로필 업데이트 API 호출
                updateProfileUseCase(
                    nickname = state.profileName,
                    imageOption = imageOption,
                    profileImage = profileImageFile,
                ).onSuccess { response ->
                    Timber.d("Profile updated successfully: ${response.profileImageUrl}")
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(ProfileEditSideEffect.ShowSuccessMessage)
                    postSideEffect(ProfileEditSideEffect.NavigateBack)
                }.onError { exception ->
                    Timber.e(exception, "Failed to update profile")
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "프로필 수정에 실패했습니다.",
                        )
                    }
                    postSideEffect(ProfileEditSideEffect.ShowErrorMessage)
                }
            }
    }
