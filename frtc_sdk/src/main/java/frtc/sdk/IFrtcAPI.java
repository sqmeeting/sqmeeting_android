package frtc.sdk;

public interface IFrtcAPI {
    String getSDKVersion();
    void setServerAddress(String address);
    String getServerAddress();
}
