package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

enum class PostDropDownActionType {
    IDLE,
    EDIT,
    DELETE,
    SAVE_IMAGE,
    REPORT
}

@Composable
fun PostDropDownMenuIcon(
    isMyPost: Boolean,
    isMenuExpanded: Boolean,
    onMenuClick: (Boolean) -> Unit,
    onDropDownMenuClick: (PostDropDownActionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { onMenuClick(!isMenuExpanded) },
            modifier = Modifier.size(24.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_meatball_menu),
                contentDescription = stringResource(id = R.string.feed_post_card_options_menu_description),
                tint = SixpackTheme.colors.gray800,
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { onMenuClick(!isMenuExpanded) },

            offset = DpOffset(x = 0.dp, y = (-1).dp),
            shape = SixpackTheme.shapes.round12,
            containerColor = SixpackTheme.colors.gray0,
            shadowElevation = 8.dp,

            properties = PopupProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            if (isMyPost) {
                MyPostMenuItems(
                    onMenuClick = { onMenuClick(!isMenuExpanded) },
                    onDropDownMenuClick = onDropDownMenuClick
                )
            } else {
                FriendPostMenuItems(
                    onMenuClick = { onMenuClick(!isMenuExpanded) },
                    onDropDownMenuClick = onDropDownMenuClick
                )
            }
        }
    }
}


/**
 * 내 게시물에 표시될 메뉴 아이템 목록
 * (ColumnScope의 확장 함수로 선언)
 */
@Composable
private fun MyPostMenuItems(
    onMenuClick: () -> Unit,
    onDropDownMenuClick: (PostDropDownActionType) -> Unit,
) {
    CustomDropdownMenuItem(
        text = stringResource(id = R.string.feed_post_dropdown_menu_edit),
        onClick = {
            onMenuClick()
            onDropDownMenuClick(PostDropDownActionType.EDIT)
        }
    )

    HorizontalDivider(
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 4.dp),
        color = SixpackTheme.colors.gray50
    )

    CustomDropdownMenuItem(
        text = stringResource(id = R.string.feed_post_dropdown_menu_delete),
        onClick = {
            onMenuClick()
            onDropDownMenuClick(PostDropDownActionType.DELETE)
        }
    )

    HorizontalDivider(
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 4.dp),
        color = SixpackTheme.colors.gray50
    )

    CustomDropdownMenuItem(
        text = stringResource(id = R.string.feed_post_dropdown_menu_save_image),
        onClick = {
            onMenuClick()
            onDropDownMenuClick(PostDropDownActionType.SAVE_IMAGE)
        }
    )
}

/**
 * 타인 게시물에 표시될 메뉴 아이템 목록
 * (ColumnScope의 확장 함수로 선언)
 */
@Composable
private fun FriendPostMenuItems(
    onMenuClick: () -> Unit,
    onDropDownMenuClick: (PostDropDownActionType) -> Unit,
) {
    CustomDropdownMenuItem(
        text = stringResource(id = R.string.feed_post_dropdown_menu_report),
        onClick = {
            onMenuClick()
            onDropDownMenuClick(PostDropDownActionType.REPORT)
        }
    )
}

@Composable
private fun CustomDropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .noRippleClickable(
                onClick = onClick,
            )
            .padding(horizontal = 12.dp),
    ) {
        Text(
            text = text,
            color = SixpackTheme.colors.gray900,
            style = SixpackTheme.typography.b2Regular,
            modifier = Modifier.widthIn(min = 120.dp)

        )
    }
}


@Preview
@Composable
fun MyPostDropDownMenuPreview() {
    DoRunPreviewWrapper {
        var isMenuExpanded by remember { (mutableStateOf(true)) }
        Column(Modifier.fillMaxSize()) {
            PostDropDownMenuIcon(
                true,
                isMenuExpanded = isMenuExpanded,
                onMenuClick = { isMenuExpanded = !isMenuExpanded },
                onDropDownMenuClick = {}
            )
        }
    }
}

@Preview
@Composable
fun FriendPostDropDownMenuPreview() {
    DoRunPreviewWrapper {
        var isMenuExpanded by remember { (mutableStateOf(true)) }
        Column(Modifier.fillMaxSize()) {
            PostDropDownMenuIcon(
                false,
                isMenuExpanded = isMenuExpanded,
                onMenuClick = { isMenuExpanded = !isMenuExpanded },
                onDropDownMenuClick = {}
            )
        }
    }
}
