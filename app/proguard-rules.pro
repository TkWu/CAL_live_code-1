# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android_WorkSpace/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 壓縮等級
-optimizationpasses 5
# 不混合大小寫
-dontusemixedcaseclassnames
# 不去忽略非公用的類別
-dontskipnonpubliclibraryclasses
-dontpreverify
# 混淆時是否記錄日誌
-verbose
# 混淆時的演算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-dontoptimize
-dontwarn
-dontskipnonpubliclibraryclassmembers


# 移除Log
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}


#解決 Gson 被 Obfuscated 後會出現 ClassCastException
##---------------Begin: proguard configuration for Gson ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
#gson
#-libraryjars libs/gson-2.6.2.jar
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# 保護註解
# For using GSON @Expose annotation
-keepattributes *Annotation*


##---------------End: proguard configuration for Gson  ----------


#解決 ActionbarSherlock 被 Obfuscated 後會出現 NoSuchMethodException
##---------------Begin: proguard configuration for Actionbar Sherlock ----------
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }
-dontwarn android.support.v4.**

-keep class android.support.v7.** { *; }
-keep class android.support.v8.** { *; }
-keep class android.support.annotation.** { *; }
-keep class android.support.graphics.drawable.** { *; }
-keep class com.google.android.** { *; }
-keep class android.support.** { *; }

-dontwarn android.support.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

-keepattributes *Annotation*
##---------------End: proguard configuration for Actionbar Sherlock  ----------

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,includedescriptorclasses,allowshrinking class * {
    native <methods>;
}

-keep class * {
    public void *(android.view.View);
}

# This will avoid all the onClick listeners referenced from XML Layouts from being removed
-keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


#不混淆任何繼承「Serializable」的元件及其成員
# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn’t save them.
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {*;}

-dontwarn android.app.Notification
#-ignorewarnings

# webview + js
-keepattributes *JavascriptInterface*
# keep 使用 webview 的类
-keepclassmembers class com.veidy.activity.WebViewActivity {
   public *;
}
# keep 使用 webview 的类的所有的内部类
-keepclassmembers  class com.veidy.activity.WebViewActivity$*{
    *;
}

####################SOAP访问第三方jar ksoap2-android.jar（开始）#####################
-dontwarn org.kobjects.**
-keep class org.kobjects.** {*;}
-dontwarn org.ksoap2.**
-keep class org.ksoap2.** {*;}
-dontwarn org.kxml2.**
-keep class org.kxml2.** {*;}
-dontwarn org.xmlpull.**
-keep class org.xmlpull.** {*;}
####################SOAP访问第三方jar ksoap2-android.jar（结束）#####################

# com.j256.ormlite.*
-keep class com.j256.ormlite.** { *; }
# 忽略警告
-dontwarn com.j256.ormlite.**

# com.mobilerq.*
-keep class com.mobilerq.android.sdk.** { *; }
-dontwarn com.mobilerq.**

# zxing*
-keep class com.google.zxing.** { *; }

-keep class android.support.multidex.** { *; }

-keep class com.squareup.timessquare.** { *; }

#fabric
-keep class io.fabric.sdk.android.** { *; }
-dontwarn io.fabric.sdk.android.**
-keep class io.fabric.** { *; }
-dontwarn io.fabric.**
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
#FB
-keep class com.facebook.** {*;}
-dontwarn com.facebook.**
-dontwarn bolts.**
-keep class bolts.** { *; }
#YouTube Utility
-keep class com.google.android.** { *; }
-dontwarn org.mozilla.**
-dontwarn com.google.tagmanager.**
-dontwarn com.google.android.youtube.**
-dontwarn info.guardianproject.netcipher.**
-dontwarn com.google.analytics.tracking.**

#WS 的class 不能混淆
-keep class ci.ws.Models.entities.** { *; }
-keep class ci.ws.Presenter.Listener.** { *; }
-keep class ci.ws.cores.object.** { *; }
-keep class ci.ui.object.item.** { *; }
#-keep class ci.ws.Models.CIAuthWSModel { *; }

-keep class ci.function.About.CIServiceAreaActivity { *; }

#RxBinding
-keep class com.jakewharton.rxbinding.** { *; }
-dontwarn com.jakewharton.rxbinding.**
-keep class rx.internal.util.** { *; }
-dontwarn rx.internal.util.**

#maps
-keep class com.google.maps.android.** { *; }
-dontwarn com.google.maps.android.**