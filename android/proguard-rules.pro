# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# signingConfig buildTypes.release.signingConfig property
# for each build variant.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Kotlin
-keep class kotlin.metadata.internal.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class * {
    *** Companion;
}

# Coroutines
-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }

# AndroidX
-keepnames class androidx.** { *; }

# Google Play Services
-keep class com.google.android.gms.** { *; }
