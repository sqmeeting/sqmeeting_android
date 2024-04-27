package frtc.sdk.ui.store;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import frtc.sdk.log.Log;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.util.AssertUtil;


public class LocalStoreBuilder {

    private static final String TAG = LocalStoreBuilder.class.getSimpleName();

    private static LocalStoreBuilder localStoreBuilder;

    private static String destFilePath;
    private static final String settingFileName = "user.setting";

    private LocalStore localStore = null;
    private final static Gson gson = new Gson();

    private LocalStoreBuilder(Context context) {
        String configPath = context.getFilesDir().getAbsolutePath();
        destFilePath = configPath + "/" + settingFileName;
        Log.d(TAG,"destFilePath:"+destFilePath + "   settingFileName:"+settingFileName);
        AssertUtil.copyAssetToFile(context, settingFileName, destFilePath);
        localStore = loadLocalStore();
        Log.d(TAG,"localStore loadLocalStore:"+ localStore);
        if(localStore == null){
            localStore = new LocalStore();
        }
        String clientId = localStore.getClientId();
        if (clientId.isEmpty()) {
            clientId = UUID.randomUUID().toString();
            localStore.setClientId(clientId);
            setLocalStore(localStore);
            Log.i(TAG, "init clientId = " + clientId);
        }
    }

    public static LocalStoreBuilder getInstance(Context context) {
        if (localStoreBuilder == null) {
            synchronized (LocalStoreBuilder.class) {
                if (localStoreBuilder == null) {
                    localStoreBuilder = new LocalStoreBuilder(context);
                }
            }
        }
        return localStoreBuilder;
    }

    public synchronized LocalStore getLocalStore() {
        return localStore;
    }


    public synchronized void setLocalStore(LocalStore config) {
        localStore = config;
        writeStrToFile(gson.toJson(config), destFilePath);
    }

    private void writeStrToFile(String str, String filePath) {
        File file = new File(filePath);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(str);
            fw.flush();
        } catch (Exception e) {
            Log.e(TAG, "Exception" + e);
        } finally {
            if(fw != null) {
                try {
                    fw.close();
                } catch (Exception e) {
                    Log.e(TAG, "Exception " + e);
                }
            }
        }
    }

    private LocalStore loadLocalStore() {
        try {
            File tryRead = new File(destFilePath);
            if (!tryRead.exists() || !tryRead.canRead()) {
                Log.e(TAG,"loadLocalStore return null");
                return null;
            }
            String content = getContentFromFile(destFilePath);
            return gson.fromJson(content, LocalStore.class);
        } catch (Exception e) {
        }
        return null;
    }

    private String getContentFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Exception " + e);
                }
            }
        }
        return sb.toString();
    }

}
