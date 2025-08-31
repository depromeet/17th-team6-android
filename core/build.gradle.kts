import java.io.FileInputStream
import java.util.Properties

val properties = Properties().apply {
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
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)
}
