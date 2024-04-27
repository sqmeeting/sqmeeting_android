package frtc.sdk.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import frtc.sdk.log.Log;

public class AssertUtil {

    private static final int bufferSize = 1024;

    public static void copyAssetToFile(Context context, String assetFile, String dst) {
        byte[] buf = new byte[bufferSize];
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            File destFile = new File(dst);
            if(destFile.exists()) {
                return;
            }
            inputStream = context.getAssets().open(assetFile);
            outputStream = new FileOutputStream(destFile);
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            Log.e("AssertUtil", "Exception " + e);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Log.e("AssertUtil", "Exception " + e);
                }
            }
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    Log.e("AssertUtil", "Exception " + e);
                }
            }
        }
    }

}
