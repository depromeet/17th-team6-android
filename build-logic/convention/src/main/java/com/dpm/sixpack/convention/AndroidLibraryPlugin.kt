package com.dpm.sixpack.convention

import com.android.build.gradle.LibraryExtension
import com.dpm.sixpack.convention.extensions.configureKotlinAndroid
import com.dpm.sixpack.convention.extensions.configureKotlinCoroutine
import com.dpm.sixpack.convention.extensions.getBundle
import com.dpm.sixpack.convention.extensions.getLibrary
import com.dpm.sixpack.convention.extensions.implementation
import com.dpm.sixpack.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			pluginManager.apply("com.android.library")

			extensions.configure<LibraryExtension> {
				configureKotlinAndroid(this)
				configureKotlinCoroutine(this)
			}

			dependencies {
				implementation(libs.getLibrary("inject"))
				implementation(libs.getLibrary("timber"))
				implementation(libs.getBundle("androidx-core"))
			}
		}
	}
}
