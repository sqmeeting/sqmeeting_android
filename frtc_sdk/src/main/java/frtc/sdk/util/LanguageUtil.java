package frtc.sdk.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtil {
    private final String TAG = getClass().getSimpleName();

    public static void setLanguage(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getAppLocale(context);
        config.setLocale(locale);

        LocaleList localeList = new LocaleList(getAppLocale(context));
        LocaleList.setDefault(localeList);
        config.setLocales(localeList);
        context.createConfigurationContext(config);

        Locale.setDefault(locale);
        resources.updateConfiguration(config, dm);
    }

    public static Locale getAppLocale(Context context) {

        int nLan = SettingUtil.getInstance(context).getLanguage();
        Locale androidlancode;
        if(nLan == -1){
            androidlancode = Locale.getDefault();
        }else {
            switch (nLan) {
                case 0:
                    androidlancode = new Locale("zh", "CN");//Locale.SIMPLIFIED_CHINESE;
                    break;
                case 1:
                    androidlancode = new Locale("zh", "TW");//Locale.TRADITIONAL_CHINESE;
                    break;
                case 2:
                    androidlancode = new Locale("en", "US");//Locale.ENGLISH ;
                    break;
                default:
                    androidlancode = Locale.getDefault();
                    break;
            }
        }
        return androidlancode;
    }

    public static boolean isSharePreferenceLan(Context context){
        int nLan = SettingUtil.getInstance(context).getLanguage();
        if(nLan == -1){
            return true;
        }
        Locale locale = context.getResources().getConfiguration().locale;
        String lang = locale.getLanguage() + "-"+locale.getCountry();

        String strLan = "";
        if(nLan == 0){
            strLan = "zh-CN";
        }else if(nLan == 1){
            strLan = "zh-TW";
        }else if(nLan == 2){
            strLan = "en-US";
        }

        if(lang.equals(strLan)){
            return true;
        }else {
            return false;
        }
    }

}
