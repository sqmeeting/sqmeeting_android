package frtc.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SettingUtil extends PreferenceUtil{

    private static final String SP_NAME = "com.frtc";
    private static SettingUtil instance;

    public SettingUtil(Context context) {
        super(context, SP_NAME);
    }

    public static SettingUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SettingUtil(context);
        }
        return instance;
    }

    public void clear() {
        SharedPreferences.Editor localEditor = edit();
        localEditor.clear().commit();
    }

    public void saveLanguage(int language) {
        putInt("language", language);
    }

    public int getLanguage() {
        return getInt("language", -1);
    }


}
