package frtc.sdk.internal.jni;

public abstract class AbstractFrtcSDK implements IJniFrtcApi {

    public native String init(String data);
    public native String destroy();
    public native String invoke(String name, String message);
    public abstract String callback(String name, String message);
}
