package com.dpm.sixpack.presentation.routes.profilecreation

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationIntent
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationSideEffect
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

private typealias ProfileCreationSyntax = Syntax<ProfileCreationState, ProfileCreationSideEffect>

@HiltViewModel
class ProfileCreationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    // TODO: Inject use cases
    // private val completeSignUpUseCase: CompleteSignUpUseCase,
) : BaseViewModel<ProfileCreationState, ProfileCreationIntent, ProfileCreationSideEffect>() {
    override val initialState: ProfileCreationState = ProfileCreationState()

    override val container: Container<ProfileCreationState, ProfileCreationSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: ProfileCreationIntent) {
        when (intent) {
            is ProfileCreationIntent.OnProfileNameChanged -> handleProfileNameChanged(intent.name)
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

            try {
                // TODO: Replace with actual API call
                // val result = completeSignUpUseCase(
                //     profileName = state.profileName,
                //     profileImageUri = state.profileImageUri
                // )
                delay(1000) // Simulate API call

                reduce { state.copy(isLoading = false) }

                postSideEffect(ProfileCreationSideEffect.NavigateToHome)
                Timber.d("Profile creation completed successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to complete profile creation")
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = "Failed to create profile",
                    )
                }
            }
        }

    private fun handleBackButtonClick() =
        intent {
            postSideEffect(ProfileCreationSideEffect.NavigateBack)
        }
}
