package com.dpm.sixpack.convention

import com.android.build.api.dsl.ApplicationExtension
import com.dpm.sixpack.convention.extensions.configureKotlinAndroid
import com.dpm.sixpack.convention.extensions.getLibrary
import com.dpm.sixpack.convention.extensions.getPlugin
import com.dpm.sixpack.convention.extensions.implementation
import com.dpm.sixpack.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/* For app module */
class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.run {
                apply(libs.getPlugin("android-application").get().pluginId)
                apply(libs.getPlugin("kotlin-android").get().pluginId)
                apply(libs.getPlugin("sixpack-hilt").get().pluginId)
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    applicationId = "com.dpm.sixpack"

                    targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
                    versionCode = libs.findVersion("versionCode").get().toString().toInt()
                    versionName = libs.findVersion("versionName").get().toString()

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildFeatures {
                    buildConfig = true
                }

                configureKotlinAndroid(this)
            }

            dependencies {
                implementation(libs.getLibrary("timber"))
            }
        }
    }
}
