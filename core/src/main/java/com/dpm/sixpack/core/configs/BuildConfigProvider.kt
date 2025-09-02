package com.dpm.sixpack.core.configs

import com.dpm.sixpack.core.BuildConfig
import javax.inject.Inject

interface BuildConfigProvider {
    fun getNaverMapClientId(): String

    // Add more methods for other build config values
}

class BuildConfigProviderImpl @Inject constructor() : BuildConfigProvider {
    override fun getNaverMapClientId(): String = BuildConfig.NAVERMAP_CLIENT_ID
}
