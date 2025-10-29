# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all services (required for Android components)
-keep class com.dpm.sixpack.runningservice.** { *; }

# Keep attributes for debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*