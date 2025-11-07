plugins {
    alias(libs.plugins.sixpack.android.library)
    alias(libs.plugins.sixpack.hilt)
}

android {
    namespace = "com.dpm.sixpack.background"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.location)
    implementation(libs.androidx.lifecycle.service)
}
