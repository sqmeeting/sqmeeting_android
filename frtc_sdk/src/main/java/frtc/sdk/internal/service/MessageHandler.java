package frtc.sdk.internal.service;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

public class MessageHandler extends Handler {

    private Handler.Callback callback;

    public MessageHandler(Handler.Callback callback) {
        this.callback = callback;
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        if (callback != null) {
            callback.handleMessage(message);
        }
    }

}
