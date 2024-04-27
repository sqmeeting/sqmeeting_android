package frtc.sdk.ui.component;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import frtc.sdk.R;

public class BaseToast {
    private static Toast instance;
    private static TextView tvMessage;

    public static void showToast(Context ctx, String message, int duration) {
        if (instance == null) {
            instance = new Toast(ctx);
            instance.setGravity(Gravity.CENTER, 0, 0);
            instance.setDuration(duration);
            View root = LayoutInflater.from(ctx).inflate(R.layout.toast_layout, null);
            tvMessage = (TextView) root.findViewById(R.id.tv_message);
            instance.setView(root);
        }
        tvMessage.setText(message);
        instance.show();
    }
    
}
