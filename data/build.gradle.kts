plugins {
    alias(libs.plugins.sixpack.data)
}

android {
    namespace = "com.dpm.sixpack.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
    implementation(libs.kotlinx.datetime)
    implementation(libs.play.services.location)
}
