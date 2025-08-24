plugins {
    alias(libs.plugins.sixpack.android.application)
}

android {
    namespace = "com.dpm.sixpack"
}

dependencies {
    implementation(libs.bundles.androidx.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)

    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))
}
