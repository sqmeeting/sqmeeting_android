package frtc.sdk.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionManager {

    private Context context;
    private int requestCode;

    public PermissionManager(Context context, int requestCode) {
        this.context = context;
        this.requestCode = requestCode;
    }

    public String[] getDeniedPermissionList() {
        List<String> deniedList = new ArrayList<>();
        List<String> checkList = new ArrayList<>(Arrays.asList(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA));
        for (String p : checkList) {
            int result = this.context.checkSelfPermission(p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                deniedList.add(p);
            }
        }
        if (!deniedList.isEmpty()) {
            return deniedList.toArray(new String[deniedList.size()]);
        }
        return null;
    }

    public int getRequestCode() {
        return requestCode;
    }
}