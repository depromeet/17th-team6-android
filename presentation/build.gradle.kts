plugins {
    alias(libs.plugins.sixpack.presentation)
}

android {
    namespace = "com.dpm.sixpack.presentation"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":RunningService"))

    implementation(libs.bundles.navermap)
    implementation(libs.androidx.lifecycle.service)
}
