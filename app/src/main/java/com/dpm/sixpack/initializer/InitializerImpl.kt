package com.dpm.sixpack.initializer

import android.app.Application
import javax.inject.Inject

class InitializerImpl
    @Inject
    constructor() : Initializer {
        override fun onAppCreate(application: Application) {
            // no-op
        }
    }
