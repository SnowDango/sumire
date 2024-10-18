# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-optimizations !code/simplification/arithmetic, !field/*, !class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.InputMerger
-keep class androidx.work.WorkerParameters

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-keep public class * extends androidx.glance.appwidget.action.ActionCallback


-keep public class * extends com.airbnb.android.showkase.models.ShowkaseProvider

-dontnote android.**

-dontwarn com.snowdango.presenter.history.HistoryScreenKt
-dontwarn com.snowdango.presenter.history.KoinModuleKt
-dontwarn com.snowdango.sumire.ui.component.CircleSongArtworkKt
-dontwarn com.snowdango.sumire.ui.component.ListSongCardKt
-dontwarn com.snowdango.sumire.ui.component.MusicAppImageKt
-dontwarn com.snowdango.sumire.ui.component.MusicAppTextKt
-dontwarn com.snowdango.sumire.ui.theme.SumireThemeKt
-dontwarn com.snowdango.sumire.ui.theme.glance.SumireGlanceThemeKt
-dontwarn com.snowdango.sumire.ui.viewdata.SongCardViewData
