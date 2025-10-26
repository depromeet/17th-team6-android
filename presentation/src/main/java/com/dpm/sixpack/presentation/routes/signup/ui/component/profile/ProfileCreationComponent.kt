package com.dpm.sixpack.presentation.routes.signup.ui.component.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.textfield.DoRunSignInputField
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun ProfileCreationComponent(
    profileName: String,
    profileImageUri: String?,
    onNameChanged: (String) -> Unit,
    onImagePickerClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Profile Image Frame
        Box(
            modifier = Modifier.size(97.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Profile image",
                    modifier =
                        Modifier
                            .size(97.dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .size(97.dp)
                            .clip(CircleShape)
                            .background(SixpackTheme.colors.gray200),
                )
            }

            // Edit Button (Camera Icon)
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(SixpackTheme.colors.gray600)
                        .clickable(enabled = enabled) { onImagePickerClick() },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "Edit profile picture",
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Name Input
        ProfileNameInput(
            profileName = profileName,
            onNameChanged = onNameChanged,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ProfileNameInput(
    profileName: String,
    onNameChanged: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    DoRunSignInputField(
        value = profileName,
        onValueChange = { newValue ->
            if (newValue.length <= 8) {
                onNameChanged(newValue)
            }
        },
        placeholder = stringResource(R.string.signup_placeholder_profile_name),
        helperText = stringResource(R.string.signup_helper_text_profile_name),
        helperTextRight = "${profileName.length}/8",
        enabled = enabled,
        keyboardType = KeyboardType.Text,
        modifier = modifier,
    )
}
