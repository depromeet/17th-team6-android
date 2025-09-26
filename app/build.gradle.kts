plugins {
    alias(libs.plugins.sixpack.android.application)
}

android {
    namespace = "com.dpm.sixpack"

    buildFeatures {
        compose = true
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

    implementation(libs.bundles.navigation)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.core.splashscreen)
}
