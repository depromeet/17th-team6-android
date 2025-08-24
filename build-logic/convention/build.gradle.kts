plugins {
    `kotlin-dsl`
}

group = "com.dpm.sixpack.build-logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "sixpack.android.application"
            implementationClass = "com.dpm.sixpack.convention.AndroidApplicationPlugin"
        }

        register("androidLibrary") {
            id = "sixpack.android.library"
            implementationClass = "com.dpm.sixpack.convention.AndroidLibraryPlugin"
        }

        register("composeLibrary") {
            id = "sixpack.compose.library"
            implementationClass = "com.dpm.sixpack.convention.AndroidComposePlugin"
        }

        register("javaLibrary") {
            id = "sixpack.java.library"
            implementationClass = "com.dpm.sixpack.convention.JavaLibraryPlugin"
        }

        register("hilt") {
            id = "sixpack.hilt"
            implementationClass = "com.dpm.sixpack.convention.HiltPlugin"
        }

        register("data") {
            id = "sixpack.data"
            implementationClass = "com.dpm.sixpack.convention.SixPackDataPlugin"
        }

        register("presentation") {
            id = "sixpack.presentation"
            implementationClass = "com.dpm.sixpack.convention.SixPackPresentationPlugin"
        }
    }
}
