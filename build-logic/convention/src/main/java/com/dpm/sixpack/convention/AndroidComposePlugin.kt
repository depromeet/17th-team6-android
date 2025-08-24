package com.dpm.sixpack.convention

import com.android.build.gradle.LibraryExtension
import com.dpm.sixpack.convention.extensions.configureComposeAndroid
import com.dpm.sixpack.convention.extensions.getLibrary
import com.dpm.sixpack.convention.extensions.getPlugin
import com.dpm.sixpack.convention.extensions.implementation
import com.dpm.sixpack.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/*
 For Compose Feature Module
 */
class AndroidComposePlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			pluginManager.run{
				apply("sixpack.android.library")
				apply(libs.getPlugin("compose-compiler").get().pluginId)
			}

			extensions.configure<LibraryExtension> {
				configureComposeAndroid(this)
			}

			dependencies {
				implementation(libs.getLibrary("kotlinx.immutable"))
			}
		}
	}
}
