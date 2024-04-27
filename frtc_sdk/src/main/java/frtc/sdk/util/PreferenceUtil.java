package frtc.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class PreferenceUtil {

    private SharedPreferences sp;


    public PreferenceUtil(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public Editor edit() {
        return sp.edit();
    }

    public void putInt(String key, int value) {
        Editor ed = sp.edit();
        ed.putInt(key, value);
        ed.commit();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void delete(String key){
        Editor ed = sp.edit();
        ed.remove(key);
        ed.commit();
    }

}
