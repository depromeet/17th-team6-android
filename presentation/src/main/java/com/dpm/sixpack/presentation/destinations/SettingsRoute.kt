package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

// 설정 메인 화면
@Serializable
data object SettingsRoute

// 프로필 수정 화면
@Serializable
data object SettingsProfileEditRoute

// 가입 정보 화면
@Serializable
data object SettingsAccountInfoRoute

// 푸시 알림 설정 화면
@Serializable
data object SettingsPushNotificationRoute
