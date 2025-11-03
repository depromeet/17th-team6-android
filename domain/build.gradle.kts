plugins {
    alias(libs.plugins.sixpack.android.library)
}

android {
    namespace = "com.dpm.sixpack.domain"
}

dependencies {
    implementation(libs.androidx.paging.runtime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
}
