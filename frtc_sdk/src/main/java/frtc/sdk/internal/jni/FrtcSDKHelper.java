package frtc.sdk.internal.jni;

import frtc.sdk.internal.IMessageListener;
import frtc.sdk.log.Log;

public class FrtcSDKHelper extends AbstractFrtcSDK {
    public static final String TAG = FrtcSDKHelper.class.getSimpleName();

    static {
        System.loadLibrary("rtc_sdk");
    }
    
    private IMessageListener listener;

    public FrtcSDKHelper(IMessageListener observer) {
        this.listener = observer;
    }

    public String initialize(String data) {
        return init(data);
    }

    public String onDestroy() {
        return destroy();
    }

    @Override
    public String callback(String name, String message) {
        try {
            return listener.handleMessage(name, message);
        } catch (Throwable t) {
            Log.e(TAG, "Throwable:"+t);
            return null;
        }
    }

    public String sendMessage(String name, String message) {
        return invoke(name, message);
    }

}
