# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-dontusemixedcaseclassnames
-dontpreverify

-keepattributes *Annotation*

-keep class androidx.datastore.preferences.** { *; }
-keep class io.ktor.* { *; }
-keep class coil3.* { *; }
-keep class ui.navigation.* { *; }

-dontnote kotlinx.serialization.**
