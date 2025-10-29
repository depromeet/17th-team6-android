# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all core utilities and base classes
-keep class com.dpm.sixpack.core.util.** { *; }
-keep class com.dpm.sixpack.core.base.** { *; }
-keep class com.dpm.sixpack.core.model.** { *; }

# Keep attributes for debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*