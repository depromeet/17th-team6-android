package com.dpm.sixpack.presentation.common.components.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiSelectionBottomSheet(
    isBottomSheetVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onEmojiSelected: (Emoji) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val emojis = Emoji.entries

    DoRunBottomSheetSlot(
        isBottomSheetVisible = isBottomSheetVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            emojis.forEach { emoji ->
                Image(
                    painter = painterResource(id = emoji.iconRes),
                    contentDescription = emoji.name,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .noRippleClickable {
                                onEmojiSelected(emoji)
                                onDismissRequest()
                            },
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Preview(backgroundColor = 0xFFFFFF, showBackground = true)
@Composable
private fun EmojiSelectionBottomSheetPreview() {
    DoRunPreviewWrapper {
        Column {
            EmojiSelectionBottomSheet(
                isBottomSheetVisible = true,
                onDismissRequest = {},
                onEmojiSelected = {},
            )
            EmojiSelectionBottomSheet(
                isBottomSheetVisible = true,
                onDismissRequest = {},
                onEmojiSelected = {},
            )
            EmojiSelectionBottomSheet(
                isBottomSheetVisible = true,
                onDismissRequest = {},
                onEmojiSelected = {},
            )
        }
    }
}
