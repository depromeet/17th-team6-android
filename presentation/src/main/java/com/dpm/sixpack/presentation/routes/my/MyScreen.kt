package com.dpm.sixpack.presentation.routes.my

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 요청하신 설정 화면의 메인 Composable입니다.
 * Scaffold를 사용하여 TopAppBar와 본문 콘텐츠를 구성합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    // 웹페이지를 열기 위해 현재 Context가 필요합니다.
    // Composable 내에서 Context를 가져오는 공식적인 방법입니다.
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.background(SixpackTheme.colors.gray0),
        containerColor = SixpackTheme.colors.gray0,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("설정",color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: 뒤로가기 로직 구현 */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기",
                            tint = Color.Black
                        )
                    }
                },
                // TopAppBar 스크롤 시 색상 변경을 원치 않으면 고정 색상 지정
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SixpackTheme.colors.gray0
                )
            )
        }
    ) { innerPadding ->
        // 설정 항목이 화면을 넘치지 않더라도 스크롤 가능하도록
        // LazyColumn 대신 Column + verticalScroll을 사용합니다.
        // 고정된 수의 항목에는 이 방식이 더 효율적입니다.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold가 제공하는 패딩 적용
                .verticalScroll(rememberScrollState())
        ) {
            // --- 프로필/계정 그룹 ---
            SettingsMenuItem(
                text = "프로필 수정",
                onClick = { /* TODO: 프로필 수정 화면 이동 */ }
            )
            SettingsMenuItem(
                text = "가입 정보",
                onClick = { /* TODO: 가입 정보 화면 이동 */ }
            )
            SettingsMenuItem(
                text = "푸시 알림 설정",
                onClick = { /* TODO: 푸시 알림 설정 화면 이동 */ }
            )

            // 그룹 구분을 위한 Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),color = SixpackTheme.colors.gray50 )

            // --- 정보 및 정책 그룹 ---
            SettingsMenuItem(
                text = "개인정보처리방침",
                onClick = {
                    // 요청하신 웹페이지 이동 로직입니다.
                    val url = "https://depromeet.notion.site/29645b4338b380658ea4d47294188129?source=copy_link" // 예시 URL
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // 웹 브라우저가 없는 예외적인 상황 처리
                        e.printStackTrace()
                    }
                }
            )
            SettingsMenuItem(
                text = "약관 및 정책",
                onClick = { /* TODO: 약관 및 정책 화면 이동 */ }
            )
            // 버전 정보 항목 (클릭 불가, 값 표시)
            SettingsInfoItem(
                title = "버전 정보",
                value = "0.0.1"
            )

            // 그룹 구분을 위한 Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp),color = SixpackTheme.colors.gray50 )

            // --- 계정 행동 그룹 ---
            SettingsActionItem(
                text = "로그아웃",
                onClick = { /* TODO: 로그아웃 로직 구현 */ }
            )
            SettingsActionItem(
                text = "탈퇴하기",
                onClick = { /* TODO: 탈퇴 로직 구현 */ }
            )
        }
    }
}

/**
 * 우측에 화살표(>)가 있는 표준 설정 메뉴 아이템입니다.
 * @param text 메뉴에 표시될 텍스트
 * @param onClick 메뉴 클릭 시 실행될 람다
 */
@Composable
private fun SettingsMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = SixpackTheme.typography.b1Bold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null, // 장식용 아이콘은 contentDescription을 null로 설정
            tint = SixpackTheme.colors.gray600 // 기본 텍스트보다 연한 색상
        )
    }
}

/**
 * "버전 정보"와 같이 우측에 텍스트 값을 표시하는 메뉴 아이템입니다.
 * 클릭이 불가능합니다.
 * @param title 메뉴 좌측 텍스트
 * @param value 메뉴 우측 텍스트 (버전 정보 등)
 */
@Composable
private fun SettingsInfoItem(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = SixpackTheme.typography.b1Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = SixpackTheme.typography.b1Bold,
            color = SixpackTheme.colors.gray600 // 버전 정보는 연한 색상
        )
    }
}

/**
 * "로그아웃", "탈퇴하기"와 같이 화살표나 추가 정보가 없는
 * 단순 텍스트 액션 아이템입니다.
 * @param text 메뉴에 표시될 텍스트
 * @param onClick 메뉴 클릭 시 실행될 람다
 */
@Composable
private fun SettingsActionItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = SixpackTheme.typography.b1Bold
            // TODO: "탈퇴하기" 등은 MaterialTheme.colorScheme.error 색상을 사용할 수 있습니다.
            // color = SixpackTheme.colors.red
        )
    }
}

// --- Preview ---
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SettingsScreenPreview() {
    // 앱의 실제 테마를 적용하면 더 정확한 미리보기가 가능합니다.
    DoRunPreviewWrapper {
        SettingsScreen()
    }
}
