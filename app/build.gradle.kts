import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.sixpack.android.application)
}

val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.dpm.sixpack"

    buildFeatures {
        compose = true
    }

    signingConfigs {
        signingConfigs {
            create("signedRelease") {
                storeFile = rootProject.file(localProperties["KEYSTORE_FILE"] as String)
                storePassword = localProperties["KEYSTORE_PASSWORD"] as String
                keyAlias = localProperties["KEY_ALIAS"] as String
                keyPassword = localProperties["KEY_PASSWORD"] as String
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("signedRelease")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core"))

    implementation(libs.bundles.androidx.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.test)

    implementation(libs.bundles.navigation)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.core.splashscreen)
}
