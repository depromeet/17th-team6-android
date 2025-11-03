package com.dpm.sixpack.presentation.common.components.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 일반 텍스트와 클릭 가능한 링크 텍스트를 조합한 컴포넌트
 *
 * @param normalText 앞부분에 표시될 일반 텍스트
 * @param linkText 클릭 가능한 링크로 표시될 텍스트
 * @param onLinkClick 링크 클릭 시 실행될 콜백
 * @param modifier Modifier
 */
@Composable
fun AuthClickableTextLink(
    normalText: String,
    linkText: String,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text =
            buildAnnotatedString {
                append(normalText)
                append(" ")

                pushLink(LinkAnnotation.Clickable(tag = "link") { onLinkClick() })
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(linkText)
                }
                pop()
            },
        style = SixpackTheme.typography.b2Regular,
        color = SixpackTheme.colors.gray600,
        textAlign = TextAlign.Center,
        modifier =
            modifier
                .fillMaxWidth(),
    )
}

@Preview
@Composable
private fun AuthClickableTextLinkPreview() {
    DoRunPreviewWrapper {
        AuthClickableTextLink(
            normalText = "이미 계정이 있으신가요?",
            linkText = "로그인",
            onLinkClick = {},
        )
    }
}
