# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signatures for reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Hilt and Dagger
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
}

# Keep Hilt generated components and modules
-keep @dagger.hilt.** class * { *; }
-keep @dagger.** interface * { *; }
-keep class **_HiltModules { *; }
-keep class **_HiltModules$** { *; }
-keep class **_HiltComponents { *; }
-keep class **_HiltComponents$** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Keep javax.inject annotations
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations

# Kotlin Serialization
-keepattributes InnerClasses
-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable

-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
}

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Retrofit (most rules come from the library itself)
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep data classes used in API responses
-keep class com.dpm.**.dto.** { *; }
-keep class com.dpm.**.model.** { *; }
-keep class com.dpm.**.entity.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Naver Map SDK
-keep class com.naver.maps.** { *; }
-dontwarn com.naver.maps.**