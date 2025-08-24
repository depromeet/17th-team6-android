package com.dpm.sixpack.convention

import com.dpm.sixpack.convention.extensions.androidTestImplementation
import com.dpm.sixpack.convention.extensions.getLibrary
import com.dpm.sixpack.convention.extensions.getPlugin
import com.dpm.sixpack.convention.extensions.implementation
import com.dpm.sixpack.convention.extensions.ksp
import com.dpm.sixpack.convention.extensions.kspTest
import com.dpm.sixpack.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.run {
                apply(libs.getPlugin("hilt").get().pluginId)
                apply(libs.getPlugin("ksp").get().pluginId)
            }

            dependencies {
                ksp(libs.getLibrary("hilt.android.compiler"))
                kspTest(libs.getLibrary("hilt.android.compiler"))
                implementation(libs.getLibrary("hilt.android"))
                androidTestImplementation(libs.getLibrary("hilt.android.testing"))
            }
        }
    }
}
