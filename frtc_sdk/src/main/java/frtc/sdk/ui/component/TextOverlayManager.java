package frtc.sdk.ui.component;

import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import frtc.sdk.log.Log;
import frtc.sdk.model.OverlayNotify;
import frtc.sdk.ui.FrtcMeetingActivity;
import frtc.sdk.ui.view.OverlayTextView;

public class TextOverlayManager {

    private static final String TAG = TextOverlayManager.class.getSimpleName();
    private RelativeLayout rootLayout;
    private OverlayTextView textView;

    private FrtcMeetingActivity activity;

    private final String DEFAULT_TYPE = "global";
    private final String DEFAULT_SPEED = "static";
    private final String LOW_SPEED = "slow";


    public TextOverlayManager(FrtcMeetingActivity activity, RelativeLayout rootLayout, OverlayTextView textView) {
        this.activity = activity;
        this.rootLayout = rootLayout;
        this.textView = textView;

        textView.setRepeatEndListener(new OverlayTextView.RepeatEndListener() {
            @Override
            public void onRepeatEnd() {
                rootLayout.setVisibility(View.GONE);
            }
        });
    }


    private void setText(String content, int actSpeed, int repeat, boolean rolling){
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        textView.setText(content, width, actSpeed, repeat, rolling);
        textView.setVisibility(View.VISIBLE);
    }

    public void onOrientationChanged(int width, int height, int rotation){
        Log.d(TAG,"onOrientationChanged:"+width+","+height+","+rotation);
        if(rootLayout.getVisibility() == View.VISIBLE){
            textView.resetWidth(width);
        }
    }

    public void dismiss() {
        if (rootLayout.getVisibility() == View.VISIBLE) {
            rootLayout.setVisibility(View.GONE);
        }
    }

    public void show(){
        if (rootLayout.getVisibility() == View.GONE) {
            rootLayout.setVisibility(View.VISIBLE);
            rootLayout.bringToFront();
        }
    }

    private void setPosition(int position){
        if(position == 0){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rootLayout.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_VERTICAL);
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rootLayout.setLayoutParams(params);
        }else if(position == 50){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rootLayout.getLayoutParams();
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            rootLayout.setLayoutParams(params);
        }else if(position == 100){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rootLayout.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_VERTICAL);
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rootLayout.setLayoutParams(params);
        }
    }

    public void onOverlayNotify(OverlayNotify overlayNotify){
        boolean enabled = overlayNotify.isEnabled();
        String content = overlayNotify.getContent();
        int repeat = overlayNotify.getRepeat();
        String speed = overlayNotify.getSpeed();
        String type = overlayNotify.getType();
        int verticalPosition = overlayNotify.getVerticaPosition();
        if (enabled) {
            if (DEFAULT_TYPE.equals(type)) {
                boolean needRolling = !DEFAULT_SPEED.equals(speed);
                int rollingSpeed = LOW_SPEED.equals(speed) ? 2 : 5;
                setText(content, rollingSpeed, repeat, needRolling);
                setPosition(verticalPosition);
                show();
            }
        } else {
            dismiss();
        }
    }
}
