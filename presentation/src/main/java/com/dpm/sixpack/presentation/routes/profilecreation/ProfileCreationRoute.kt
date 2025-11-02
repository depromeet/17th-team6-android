package com.dpm.sixpack.presentation.routes.profilecreation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationIntent
import com.dpm.sixpack.presentation.routes.profilecreation.contract.ProfileCreationSideEffect
import com.dpm.sixpack.presentation.routes.profilecreation.ui.screen.ProfileCreationScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileCreationRoute(
    phoneNumber: String,
    modifier: Modifier = Modifier,
    viewModel: ProfileCreationViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val screenState by viewModel.collectAsState()

    // Image picker launcher
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                viewModel.onIntent(ProfileCreationIntent.OnProfileImageSelected(it.toString()))
            }
        }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileCreationSideEffect.LaunchImagePicker -> {
                imagePickerLauncher.launch("image/*")
            }
            is ProfileCreationSideEffect.NavigateToHome -> onNavigateToHome()
            is ProfileCreationSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    ProfileCreationScreen(
        state = screenState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
