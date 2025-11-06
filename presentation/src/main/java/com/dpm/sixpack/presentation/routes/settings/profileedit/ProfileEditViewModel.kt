package com.dpm.sixpack.presentation.routes.settings.profileedit

import androidx.lifecycle.SavedStateHandle
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
        // TODO: 프로필 수정 관련 UseCase 주입
        // private val getUserProfileUseCase: GetUserProfileUseCase,
        // private val updateUserProfileUseCase: UpdateUserProfileUseCase,
        // private val convertUriToFileUseCase: ConvertUriToFileUseCase,
    ) : BaseViewModel<ProfileEditState, ProfileEditIntent, ProfileEditSideEffect>() {
        override val initialState: ProfileEditState =
            ProfileEditState(
                // TODO: 실제 사용자 프로필로 초기화
                profileName = "홍길동",
                profileImageUri = null,
            )

        override val container: Container<ProfileEditState, ProfileEditSideEffect> =
            container(initialState = initialState, savedStateHandle = savedStateHandle)

        init {
            // TODO: 실제 사용자 프로필 로드
            // loadUserProfile()
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

                // TODO: 실제 프로필 업데이트 로직
                // val profileImageFile = state.profileImageUri?.let { convertUriToFileUseCase(it).getOrNull() }
                // updateUserProfileUseCase(nickname = state.profileName, profileImage = profileImageFile)
                //     .onSuccess {
                //         reduce { state.copy(isLoading = false) }
                //         postSideEffect(ProfileEditSideEffect.ShowSuccessMessage)
                //         postSideEffect(ProfileEditSideEffect.NavigateBack)
                //     }
                //     .onError { exception ->
                //         reduce {
                //             state.copy(
                //                 isLoading = false,
                //                 errorMessage = exception.message ?: "프로필 수정에 실패했습니다."
                //             )
                //         }
                //     }

                // Mock 성공 처리
                Timber.d("Profile updated: ${state.profileName}")
                reduce { state.copy(isLoading = false) }
                postSideEffect(ProfileEditSideEffect.ProfileEditCompleted)
                postSideEffect(ProfileEditSideEffect.NavigateBack)
            }
    }
