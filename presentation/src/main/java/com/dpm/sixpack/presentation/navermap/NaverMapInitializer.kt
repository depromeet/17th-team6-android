package com.dpm.sixpack.presentation.navermap

import android.app.Application
import com.dpm.sixpack.core.configs.BuildConfigProvider
import com.naver.maps.map.NaverMapSdk
import javax.inject.Inject

/*
 * This interface is used to initialize the NaverMap SDK when use standard SDK, not 3rd compose library
 */
interface AppInitializer {
    fun onAppCreate(application: Application)
}

class NaverMapInitializer
@Inject
constructor(
    private val buildConfigProvider: BuildConfigProvider,
) : AppInitializer {
    override fun onAppCreate(application: Application) {
        NaverMapSdk.getInstance(application).setClient(
            NaverMapSdk.NcpKeyClient(buildConfigProvider.getNaverMapClientId()),
        )
    }
}
