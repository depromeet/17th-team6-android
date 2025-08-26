package com.dpm.sixpack.convention

import com.dpm.sixpack.convention.extensions.getBundle
import com.dpm.sixpack.convention.extensions.getLibrary
import com.dpm.sixpack.convention.extensions.getPlugin
import com.dpm.sixpack.convention.extensions.implementation
import com.dpm.sixpack.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SixPackDataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("sixpack.android.library")
                apply("sixpack.hilt")
                apply(libs.getPlugin("kotlin-parcelize").get().pluginId)
            }

            dependencies {
                implementation(libs.getBundle("network"))
                implementation(libs.getLibrary("androidx-datastore-preferences"))
                implementation(libs.getLibrary("androidx-room"))
            }
        }
    }
}
