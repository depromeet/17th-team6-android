plugins {
    alias(libs.plugins.sixpack.android.application)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
}

android {
    namespace = "com.dpm.sixpack"

    buildFeatures {
        compose = true
    }

    lint {
        // Hilt가 custom AppComponentFactory를 사용하므로
        // LifecycleService에 대한 Instantiatable 체크는 false positive
        disable += "Instantiatable"
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":background"))

    implementation(libs.bundles.androidx.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)

    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.coil)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.core.splashscreen)

    // Orbit MVI for MainViewModel
    implementation(libs.bundles.orbit)
}
