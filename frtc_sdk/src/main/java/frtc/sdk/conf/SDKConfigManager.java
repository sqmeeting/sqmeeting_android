package frtc.sdk.conf;

import android.content.Context;

public class SDKConfigManager {

    private static SDKConfigManager SDKConfigManager;

    private String clientId = "";
    private String serverAddress = "";
    private String sdkVersion = "";

    private SDKConfigManager(Context context) {
    }

    public static SDKConfigManager getInstance(Context context) {
        if (SDKConfigManager == null) {
            synchronized (SDKConfigManager.class) {
                if (SDKConfigManager == null) {
                    SDKConfigManager = new SDKConfigManager(context);
                }
            }
        }
        return SDKConfigManager;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getSDKVersion() {
        return sdkVersion;
    }

    public void setSDKVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
}
