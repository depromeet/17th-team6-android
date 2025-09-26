plugins {
    alias(libs.plugins.sixpack.presentation)
}

android {
    namespace = "com.dpm.sixpack.presentation"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":background"))

    implementation(libs.bundles.navermap)
    implementation(libs.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.service)
}
