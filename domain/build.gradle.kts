plugins {
    alias(libs.plugins.sixpack.android.library)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.dpm.sixpack.domain"
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.bundles.paging)
    androidTestImplementation(libs.bundles.android.test)
}
