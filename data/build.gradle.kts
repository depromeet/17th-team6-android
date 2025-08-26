plugins {
    alias(libs.plugins.sixpack.data)
}

android {
    namespace = "com.dpm.sixpack.data"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)

    implementation(project(":domain"))
}
