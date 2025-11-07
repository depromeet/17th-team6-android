import java.io.FileInputStream
import java.util.Properties

val properties =
    Properties()
        .apply {
            load(FileInputStream(rootProject.file("local.properties")))
        }

plugins {
    alias(libs.plugins.sixpack.android.library)
    alias(libs.plugins.sixpack.hilt)
}

android {
    namespace = "com.dpm.sixpack.core"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "NAVERMAP_CLIENT_ID", "\"${properties.getProperty("NAVERMAP_CLIENT_ID")}\"")
        buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.versionName.get()}\"")
    }
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.coil)
    androidTestImplementation(libs.bundles.android.test)
    implementation(libs.androidx.activity.compose)
}
