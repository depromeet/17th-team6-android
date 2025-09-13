package com.dpm.sixpack.initializer

import android.app.Application

/*
 * This interface is used to initialize the NaverMap SDK when use standard SDK, not 3rd compose library
 */
interface Initializer {
    fun onAppCreate(application: Application)
}
