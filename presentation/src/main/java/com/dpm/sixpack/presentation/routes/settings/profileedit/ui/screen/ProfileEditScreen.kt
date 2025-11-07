package com.dpm.sixpack.presentation.routes.settings.profileedit.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTitleTopBar
import com.dpm.sixpack.presentation.routes.settings.profileedit.contract.ProfileEditIntent
import com.dpm.sixpack.presentation.routes.settings.profileedit.contract.ProfileEditState
import com.dpm.sixpack.presentation.routes.signup.ui.component.profile.ProfileCreationComponent
import com.dpm.sixpack.presentation.theme.SixPackDimen
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun ProfileEditScreen(
    state: ProfileEditState,
    onIntent: (ProfileEditIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunTitleTopBar(
                title = stringResource(R.string.settings_profile_edit),
                onBackClick = { onIntent(ProfileEditIntent.OnBackButtonClick) },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = SixPackDimen.defaultSideMargin),
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // 프로필 수정 컴포넌트 (ProfileCreationComponent 재사용)
                ProfileCreationComponent(
                    profileName = state.profileName,
                    profileImageUri = state.profileImageUri,
                    onNameChanged = { onIntent(ProfileEditIntent.OnProfileNameChanged(it)) },
                    onImagePickerClick = { onIntent(ProfileEditIntent.OnPickImageClick) },
                    enabled = !state.isLoading,
                    isProfileNameValid = state.isProfileNameValid,
                    modifier = Modifier.fillMaxWidth(),
                )

                // 에러 메시지
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.errorMessage,
                        style = SixpackTheme.typography.c1Regular,
                        color = SixpackTheme.colors.red,
                    )
                }
            }

            // 완료 버튼
            DoRunDefaultButton(
                text = stringResource(R.string.common_complete),
                onClick = {
                    onIntent(ProfileEditIntent.OnCompleteClick)
                },
                enabled = state.isCompleteButtonEnabled,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .consumeWindowInsets(paddingValues)
                        .imePadding()
                        .padding(horizontal = SixPackDimen.defaultSideMargin)
                        .padding(bottom = 12.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ProfileEditScreenPreview() {
    DoRunPreviewWrapper {
        ProfileEditScreen(
            state =
                ProfileEditState(
                    profileName = "홍길동",
                    profileImageUri = null,
                ),
            onIntent = {},
        )
    }
}
