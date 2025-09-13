package com.dpm.sixpack

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.dpm.sixpack.presentation.navermap.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SixPackApp : Application() {
    @Inject
    lateinit var initializers: AppInitializer

    override fun onCreate() {
        super.onCreate()

        initializers.onAppCreate(this)

        setDarkMode()
        initTimber()
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
