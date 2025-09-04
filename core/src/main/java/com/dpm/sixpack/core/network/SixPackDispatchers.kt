package com.dpm.sixpack.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(
    val dispatcher: SixPackDispatchers,
)

enum class SixPackDispatchers {
    Default,
    IO,
}
