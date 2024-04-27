package frtc.sdk.internal.jni;

public interface IJniFrtcApi {

    String invoke(String name, String message);
    String callback(String name, String message);
}
