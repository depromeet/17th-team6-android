# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all data layer classes
-keep class com.dpm.sixpack.data.** { *; }

# Keep all Hilt modules in data
-keep @dagger.Module class com.dpm.sixpack.data.** { *; }
-keep @dagger.hilt.InstallIn class com.dpm.sixpack.data.** { *; }

# Keep all repositories
-keep class com.dpm.sixpack.data.repository.** { *; }

# Keep all data sources
-keep class com.dpm.sixpack.data.source.** { *; }

# Keep attributes for debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*