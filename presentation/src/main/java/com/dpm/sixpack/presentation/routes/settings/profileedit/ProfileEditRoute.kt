package com.dpm.sixpack.presentation.routes.settings.profileedit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.settings.profileedit.contract.ProfileEditIntent
import com.dpm.sixpack.presentation.routes.settings.profileedit.contract.ProfileEditSideEffect
import com.dpm.sixpack.presentation.routes.settings.profileedit.ui.screen.ProfileEditScreen

@Composable
fun ProfileEditRoute(
    onNavigateBack: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsState()

    val profileEditCompletedMessage = stringResource(R.string.settings_profile_edit_completed_snackbar)

    // 이미지 선택 런처
    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            uri?.let {
                viewModel.onIntent(ProfileEditIntent.OnProfileImageSelected(it.toString()))
            }
        }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                ProfileEditSideEffect.NavigateBack -> onNavigateBack()
                ProfileEditSideEffect.LaunchImagePicker -> {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                }
                ProfileEditSideEffect.ProfileEditCompleted -> {
                    onShowSnackbar(profileEditCompletedMessage, null)
                }
            }
        }
    }

    ProfileEditScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
