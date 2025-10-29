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
-keep class com.dpm.sixpack.core.configs.** { *; }

# Keep all Hilt modules in core
-keep @dagger.Module class com.dpm.sixpack.core.** { *; }
-keep @dagger.hilt.InstallIn class com.dpm.sixpack.core.** { *; }

# Keep dispatcher and coroutine scope modules explicitly
-keep class com.dpm.sixpack.core.network.di.** { *; }
-keep class com.dpm.sixpack.core.di.** { *; }

# Keep network related classes
-keep class com.dpm.sixpack.core.network.** { *; }

# Keep attributes for debugging
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*