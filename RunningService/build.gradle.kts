plugins {
    alias(libs.plugins.sixpack.android.library)
    alias(libs.plugins.sixpack.hilt)
}

android {
    namespace = "com.dpm.sixpack.runningservice"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.location)
    implementation(libs.androidx.lifecycle.service)
}
