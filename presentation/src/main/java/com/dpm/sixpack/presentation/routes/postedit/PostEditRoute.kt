package com.dpm.sixpack.presentation.routes.postedit

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dpm.sixpack.core.permission.SixPackPermissions
import com.dpm.sixpack.presentation.common.util.PermissionHandler
import com.dpm.sixpack.presentation.routes.postedit.contract.PostEditIntent
import com.dpm.sixpack.presentation.routes.postedit.contract.PostEditSideEffect
import com.dpm.sixpack.presentation.routes.postedit.ui.PostEditScreen
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.io.IOException

@Composable
fun PostEditRoute(
    feedId: Long,
    viewModel: PostEditViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.collectAsState()

    // Post 데이터 로드
    LaunchedEffect(feedId) {
        viewModel.loadPost(feedId)
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // 이미지 선택 Launcher
    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri: Uri? ->
            uri?.let { viewModel.onIntent(PostEditIntent.OnImageSelected(it)) }
        }

    // 권한 요청 Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val isGranted = permissions.values.all { it }
            viewModel.onIntent(PostEditIntent.OnImagePermissionResult(isGranted))
            if (isGranted) {
                viewModel.onIntent(PostEditIntent.OnImageEditButtonClick)
            }
        }

    // 이미지 권한 핸들러
    PermissionHandler(
        context = context,
        lifecycleOwner = lifecycleOwner,
        permissionsToRequest = SixPackPermissions.ImagePermissions,
        onPermissionResult = { isGranted ->
            viewModel.onIntent(PostEditIntent.OnImagePermissionResult(isGranted))
        },
    )

    // SideEffect 처리
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            PostEditSideEffect.NavigateBack -> onNavigateBack()

            PostEditSideEffect.OpenImagePicker -> {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }

            PostEditSideEffect.RequestImagePermission -> {
                val permissions = SixPackPermissions.ImagePermissions.map { it.permission }.toTypedArray()
                permissionLauncher.launch(permissions)
            }

            is PostEditSideEffect.SaveImageToGallery -> {
                coroutineScope.launch {
                    saveImageToGallery(context, sideEffect.imageUrl)
                }
            }

            is PostEditSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is PostEditSideEffect.ShowError -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (!state.isLoading) {
        true -> {
            PostEditScreen(
                state = state,
                onBackButtonClick = {
                    viewModel.onIntent(PostEditIntent.OnBackClick)
                },
                onSaveIconClick = {
                    viewModel.onIntent(PostEditIntent.OnSaveClick)
                },
                onImageEditButtonClick = {
                    viewModel.onIntent(PostEditIntent.OnImageEditButtonClick)
                },
                onSubmitClick = {
                    viewModel.onIntent(PostEditIntent.OnSubmitClick)
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

// TODO SB 이미지 저장 Util로 구현 제대로 해야함
private suspend fun saveImageToGallery(
    context: Context,
    imageUrl: String,
) {
    withContext(Dispatchers.IO) {
        try {
            // URI 형식인 경우
            if (imageUrl.startsWith("content://") || imageUrl.startsWith("file://")) {
                val sourceUri = Uri.parse(imageUrl)
                val contentResolver = context.contentResolver

                // MediaStore에 이미지 저장
                val contentValues =
                    ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "DoRun_${System.currentTimeMillis()}.jpg")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DoRun")
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let { targetUri ->
                    contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                        contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    // IS_PENDING 해제
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(targetUri, contentValues, null, null)
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // HTTP URL인 경우 - TODO: 이미지 다운로드 후 저장
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "URL 이미지 저장은 아직 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "이미지 저장 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
