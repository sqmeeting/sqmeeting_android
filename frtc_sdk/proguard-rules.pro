-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-keepattributes SourceFile,LineNumberTable

-keepclassmembers enum * { *; }

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference                     
-keep public class com.android.vending.licensing.ILicensingService


-keep class frtc.sdk.internal.jni.support.** { *; }
-keep class frtc.sdk.internal.model.** {*;}
-keep class frtc.sdk.internal.jni.AbstractFrtcSDK { *; }
-keep class frtc.sdk.internal.jni.Renderer { *; }

-keep class frtc.sdk.interfaces.** { *; }
-keep class frtc.sdk.log.** { *; }

-keep class frtc.sdk.ui.config.** { *; }

-keep class frtc.sdk.ui.dialog.MeetingReminderDlg {*;}
-keep class frtc.sdk.ui.dialog.InvitationInformationDlg {*;}

-keep public class frtc.sdk.FrtcCall {*;}
-keep public class * implements frtc.sdk.IFrtcCallAPI

-keep public class frtc.sdk.FrtcManagement {*;}
-keep public class * implements frtc.sdk.IFrtcManagementAPI

-keep public class frtc.sdk.IFrtcCallListener {*;}
-keep public class * implements frtc.sdk.IFrtcCallListener

-keep public class frtc.sdk.IFrtcManagementListener {*;}
-keep public class * implements frtc.sdk.IFrtcManagementListener

-keep public class frtc.sdk.IMeetingReminderListener {*;}
-keep public class * implements frtc.sdk.IMeetingReminderListener

-keep public class frtc.sdk.util.JSONUtil {*;}
-keep public class frtc.sdk.util.FrtcHttpClient {*;}
-keep public class frtc.sdk.util.StringUtils {*;}
-keep public class frtc.sdk.util.ActivityUtils {*;}
-keep public class frtc.sdk.util.LanguageUtil {*;}
-keep public class frtc.sdk.util.SettingUtil {*;}
-keep public class frtc.sdk.util.BaseDialog {*;}
-keep public class frtc.sdk.util.MeetingUtil {*;}
-keep public class frtc.sdk.util.MathUtil {*;}
-keep public class frtc.sdk.internal.mapper.** {*;}
-keep public class * implements frtc.sdk.internal.mapper.**
-keep class frtc.sdk.ui.layout.GalleryLayoutData {*;}

-keep public class frtc.sdk.model.** { *; }
-keep public class frtc.sdk.* { *; }
-keep public class frtc.sdk.internal.model.* { *; }


-keep class com.google.** { *; }
-keep interface com.google.** { *; }


-keepattributes Signature
-keepattributes Annotation
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }


-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
        *** get*();
        void set*(***);
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep class * implements java.io.Serializable {
    public static final android.os.Parcelable$Creator *;
}


