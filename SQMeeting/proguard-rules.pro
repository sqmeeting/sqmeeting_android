-optimizationpasses 5
-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify

-keepattributes *Annotation*,InnerClasses

-keepattributes Signature

-verbose
-keepattributes SourceFile,LineNumberTable


-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application

-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

-keep class android.support.** {*;}
-dontwarn android.support.**

-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keep class **.R$* {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keepclassmembers enum * { *; }

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class * implements java.io.Serializable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keep class sun.security.** { *; }
-keep class com.google.gson.** { *; }
-keep class android.os.** {*;}

-keep class frtc.sdk.internal.jni.support.** { *; }
-keep class frtc.sdk.internal.jni.AbstractFrtcSDK { *; }
-keep class frtc.sdk.internal.jni.Renderer { *; }
-keep class frtc.sdk.internal.service.ManagementService{ *; }


-keep public class frtc.sdk.FrtcCall {*;}
-keep public class * implements frtc.sdk.IFrtcCallAPI

-keep public class frtc.sdk.FrtcManagement {*;}
-keep public class * implements frtc.sdk.IFrtcManagementAPI

-keep public class frtc.sdk.IFrtcCallListener {*;}
-keep public class * implements frtc.sdk.IFrtcCallListener

-keep public class frtc.sdk.IFrtcManagementListener {*;}
-keep public class * implements frtc.sdk.IFrtcManagementListener

-keep public class frtc.sdk.util.JSONUtil {*;}
-keep public class frtc.sdk.util.FrtcHttpClient {*;}
-keep public class frtc.sdk.internal.mapper.** {*;}
-keep public class * implements frtc.sdk.internal.mapper.**
-keep class frtc.sdk.ui.layout.GalleryLayoutData {*;}

-keep public class frtc.sdk.model.** { *; }
-keep public class frtc.sdk.* { *; }
-keep public class frtc.sdk.internal.model.* { *; }
-keep public class com.frtc.sqmeetingce.ui.config.* { *; }

-keep class com.google.** { *; }
-keep interface com.google.** { *; }

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }

