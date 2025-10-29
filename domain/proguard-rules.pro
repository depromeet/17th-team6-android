# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all domain layer classes
-keep class com.dpm.sixpack.domain.** { *; }

# Keep all domain models, use cases, and utility classes
-keep class com.dpm.sixpack.domain.model.** { *; }
-keep class com.dpm.sixpack.domain.usecase.** { *; }
-keep class com.dpm.sixpack.domain.util.** { *; }
-keep class com.dpm.sixpack.domain.exception.** { *; }
-keep interface com.dpm.sixpack.domain.repository.** { *; }

# Keep attributes for debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*