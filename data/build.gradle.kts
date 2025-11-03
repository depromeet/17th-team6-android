plugins {
    alias(libs.plugins.sixpack.data)
}

android {
    namespace = "com.dpm.sixpack.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))

    implementation(libs.androidx.paging.runtime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
    implementation(libs.play.services.location)
}
