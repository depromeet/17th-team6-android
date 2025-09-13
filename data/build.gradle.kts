plugins {
    alias(libs.plugins.sixpack.data)
}

android {
    namespace = "com.dpm.sixpack.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":background"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
    implementation(libs.kotlinx.datetime)
    implementation(libs.play.services.location)
    implementation(libs.androidx.lifecycle.service)
}
