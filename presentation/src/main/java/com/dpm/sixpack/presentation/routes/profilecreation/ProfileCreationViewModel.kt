package com.dpm.sixpack.presentation.routes.profilecreation

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.domain.usecase.ConvertUriToFileUseCase
import com.dpm.sixpack.domain.usecase.SignUpUseCase
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationIntent
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationSideEffect
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileCreationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val signUpUseCase: SignUpUseCase,
    private val convertUriToFileUseCase: ConvertUriToFileUseCase,
) : BaseViewModel<ProfileCreationState, ProfileCreationIntent, ProfileCreationSideEffect>() {
    private val phoneNumber: String =
        savedStateHandle.get<String>("phoneNumber") ?: ""

    override val initialState: ProfileCreationState = ProfileCreationState(phoneNumber = phoneNumber)

    override val container: Container<ProfileCreationState, ProfileCreationSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: ProfileCreationIntent) {
        when (intent) {
            is ProfileCreationIntent.OnProfileNameChanged -> handleProfileNameChanged(intent.name)
            is ProfileCreationIntent.OnPickImageClick -> handlePickImage()
            is ProfileCreationIntent.OnProfileImageSelected -> handleProfileImageSelected(intent.imageUri)
            is ProfileCreationIntent.OnCompleteProfileClick -> handleCompleteProfile()
            is ProfileCreationIntent.OnBackButtonClick -> handleBackButtonClick()
        }
    }

    private fun handleProfileNameChanged(name: String) =
        intent {
            reduce {
                state.copy(
                    profileName = name,
                    errorMessage = null,
                )
            }
        }

    private fun handlePickImage() =
        intent {
            postSideEffect(ProfileCreationSideEffect.LaunchImagePicker)
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

    private fun handleCompleteProfile() =
        intent {
            if (!state.isProfileNameValid) {
                reduce {
                    state.copy(errorMessage = "Profile name is required")
                }
                return@intent
            }

            reduce { state.copy(isLoading = true) }

            // Convert profileImageUri to File if exists
            var profileImageFile: java.io.File? = null
            state.profileImageUri?.let { uriString ->
                val fileResult = convertUriToFileUseCase(uriString)
                fileResult
                    .onSuccess { file ->
                        profileImageFile = file
                    }.onError { exception ->
                        Timber.w("Failed to convert image URI to file: ${exception.message}")
                        // Continue with null profileImage
                    }
            }

            val result =
                signUpUseCase(
                    nickname = state.profileName,
                    phoneNumber = state.phoneNumber,
                    profileImage = profileImageFile,
                )

            result
                .onSuccess { signUpResult ->
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(ProfileCreationSideEffect.NavigateToHome)
                    Timber.d("Profile creation completed successfully: ${signUpResult.user.nickname}")
                }.onError { exception ->
                    Timber.e("Failed to complete profile creation: ${exception.message}")
                    reduce {
                        state.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to create profile",
                        )
                    }
                }
        }

    private fun handleBackButtonClick() =
        intent {
            postSideEffect(ProfileCreationSideEffect.NavigateBack)
        }
}
