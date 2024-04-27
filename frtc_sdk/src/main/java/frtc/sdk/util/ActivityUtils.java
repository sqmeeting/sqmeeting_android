package frtc.sdk.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.log.Log;
import frtc.sdk.ui.FrtcMeetingActivity;

public class ActivityUtils {
    protected static final String TAG = "ActivityUtils";
    public static List<Activity> acys = new ArrayList<Activity>();


    public static void add(Activity acy) {
        acys.add(acy);
    }

    public static void remove(Activity acy) {
        acys.remove(acy);
    }

    public static boolean isExistMeetingActivity() {
        int size = acys.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity act = acys.get(i);
            if (act instanceof FrtcMeetingActivity) {
                return true;
            }
        }
        return false;
    }
}
