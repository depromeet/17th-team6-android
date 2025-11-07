package com.dpm.sixpack.presentation.routes.postupload

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.core.permission.SixPackPermissions
import com.dpm.sixpack.presentation.common.util.PermissionHandler
import com.dpm.sixpack.presentation.routes.postupload.contract.PostUploadIntent
import com.dpm.sixpack.presentation.routes.postupload.contract.PostUploadSideEffect
import com.dpm.sixpack.presentation.routes.postupload.ui.PostUploadScreen
import com.dpm.sixpack.presentation.theme.SixpackTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun PostUploadRoute(
    viewModel: PostUploadViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToFeed: () -> Unit,
) {
    val state by viewModel.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 이미지 선택 Launcher
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri: Uri? ->
            uri?.let { viewModel.onIntent(PostUploadIntent.OnImageSelected(it)) }
        }

    // 권한 요청 Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val isGranted = permissions.values.all { it }
            viewModel.onIntent(PostUploadIntent.OnImagePermissionResult(isGranted))
            if (isGranted) {
                viewModel.onIntent(PostUploadIntent.OnImageEditButtonClick)
            }
        }

    // 이미지 권한 핸들러
    PermissionHandler(
        context = context,
        lifecycleOwner = lifecycleOwner,
        permissionsToRequest = SixPackPermissions.ImagePermissions,
        onPermissionResult = { isGranted ->
            viewModel.onIntent(PostUploadIntent.OnImagePermissionResult(isGranted))
        },
    )

    // SideEffect 처리
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PostUploadSideEffect.NavigateBack -> navigateBack()

            PostUploadSideEffect.OpenImagePicker -> {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }

            PostUploadSideEffect.RequestImagePermission -> {
                val permissions = SixPackPermissions.ImagePermissions.map { it.permission }.toTypedArray()
                permissionLauncher.launch(permissions)
            }

            is PostUploadSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is PostUploadSideEffect.ShowError -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            PostUploadSideEffect.NavigateToFeed -> {
                navigateToFeed()
            }
        }
    }

    when (!state.isLoading) {
        true -> {
            PostUploadScreen(
                state = state,
                onBackButtonClick = {
                    viewModel.onIntent(PostUploadIntent.OnBackClick)
                },
                onImageEditButtonClick = {
                    viewModel.onIntent(PostUploadIntent.OnImageEditButtonClick)
                },
                onUploadButtonClick = {
                    viewModel.onIntent(PostUploadIntent.OnUploadButtonClick)
                },
            )
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = SixpackTheme.colors.blue600,
                )
            }
        }
    }
}
