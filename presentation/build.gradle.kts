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

    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    implementation(libs.bundles.navermap)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.paging)
    implementation(libs.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.service)
}
