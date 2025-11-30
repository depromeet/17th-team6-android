package com.dpm.sixpack.convention

import com.android.build.api.dsl.ApplicationExtension
import com.dpm.sixpack.convention.extensions.configureComposeAndroid
import com.dpm.sixpack.convention.extensions.configureKotlinAndroid
import com.dpm.sixpack.convention.extensions.getLibrary
import com.dpm.sixpack.convention.extensions.getPlugin
import com.dpm.sixpack.convention.extensions.implementation
import com.dpm.sixpack.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import java.io.FileInputStream
import java.util.Properties

// For app module
class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Load keystore properties from local.properties
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localProperties.load(FileInputStream(localPropertiesFile))
            }

            pluginManager.run {
                apply(libs.getPlugin("android-application").get().pluginId)
                apply(libs.getPlugin("kotlin-android").get().pluginId)
                apply(libs.getPlugin("sixpack-hilt").get().pluginId)
                apply(libs.getPlugin("compose-compiler").get().pluginId)
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    applicationId = "com.dpm.sixpack"

                    targetSdk =
                        libs
                            .findVersion("targetSdk")
                            .get()
                            .toString()
                            .toInt()
                    versionCode =
                        libs
                            .findVersion("versionCode")
                            .get()
                            .toString()
                            .toInt()
                    versionName = libs.findVersion("versionName").get().toString()

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                // Configure signing for release builds
                signingConfigs {
                    create("release") {
                        storeFile = localProperties.getProperty("KEYSTORE_FILE")?.let { rootProject.file(it) }
                        storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                        keyAlias = localProperties.getProperty("KEY_ALIAS")
                        keyPassword = localProperties.getProperty("KEY_PASSWORD")
                    }
                }

                buildFeatures {
                    buildConfig = true
                }

                configureKotlinAndroid(this)
                configureComposeAndroid(this)

                // Additional release configuration
                buildTypes {
                    getByName("debug") {
                        proguardFiles(
                            getDefaultProguardFile("proguard-android.txt"),
                            "proguard-debug.pro",
                        )
                    }

                    getByName("release") {
                        signingConfig = signingConfigs.getByName("release")
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android.txt"),
                            "proguard-rules.pro",
                        )
                    }
                }
            }

            dependencies {
                implementation(libs.getLibrary("timber"))
            }
        }
    }
}
