package com.dpm.sixpack.presentation.navigation.util

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * @Serializable 어노테이션이 붙은 모든 데이터 클래스(T)를
 * NavType으로 사용할 수 있게 해주는 범용 유틸리티 함수.
 *
 * 내부적으로 KSerializer를 찾아 객체를 JSON 문자열로 직렬화/역직렬화합니다.
 *
 * @param T 변환할 데이터 클래스 타입 (reified)
 * @param isNullableAllowed 이 타입이 null을 허용하는지 여부 (기본값: false)
 * @param json 사용할 Json 인코더 (기본값: Json.Default)
 * @return NavType<T>
 */
inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
): NavType<T> {
    val serializer: KSerializer<T> = serializer()

    return object : NavType<T>(isNullableAllowed = isNullableAllowed) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): T? =
            bundle.getString(key)?.let {
                json.decodeFromString(serializer, it)
            }

        override fun put(
            bundle: Bundle,
            key: String,
            value: T,
        ) {
            bundle.putString(key, json.encodeToString(serializer, value))
        }

        override fun parseValue(value: String): T = json.decodeFromString(serializer, value)

        override fun serializeAsValue(value: T): String = json.encodeToString(serializer, value)
    }
}
