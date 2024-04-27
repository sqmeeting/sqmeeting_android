package frtc.sdk.ui.component;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.log.Log;

public class FloatView {
    private static final String TAG = FloatView.class.getSimpleName();

    private Context context;
    private View callFloatView;
    private TextView floatWinName;
    private boolean isMove = false;

    private int statusBarHeight = 0;
    private WindowManager.LayoutParams params;

    private boolean showControlBtn = true;
    private IMeetingFloatViewListener listener;

    boolean isSharingContent = false;
    private int screenWidth = 720;
    private int screenHeight = 1280;

    public FloatView(Context context, boolean sharingContent){
        this.context = context;
        isSharingContent = sharingContent;
        initView();
    }

    public View getView(){
        if(callFloatView == null){
            initView();
        }
        return callFloatView;
    }

    private void initView(){
        callFloatView = LayoutInflater.from(context).inflate(R.layout.floating_view, null);
        floatWinName = callFloatView.findViewById(R.id.tv_floating);
        if(isSharingContent){
            floatWinName.setText(context.getString(R.string.float_view_share_name));
        }else{
            floatWinName.setText(context.getString(R.string.float_view_meeting_name));
        }
        WindowManager wm = (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        callFloatView.setOnTouchListener(new FloatingOnTouchListener());
    }


    private void refreshView(int x, int y) {

        if (statusBarHeight == 0) {
            View rootView = getView().getRootView();
            Rect rect = new Rect();
            rootView.getWindowVisibleDisplayFrame(rect);
            statusBarHeight = rect.top;
        }

        params.x = x;
        params.y = y - statusBarHeight;

        Log.i(TAG, "refreshView： x:"+x+" y:"+y+" params.x:"+params.x+" params.y:" +params.y +" statusBarHeight:"+statusBarHeight);
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(WINDOW_SERVICE);
        wm.updateViewLayout(getView(),params);
    }


    public void show(){
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.RGBA_8888;

        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = screenWidth;
        params.y = 0;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        try{
            Log.d(TAG,"callFloatView.show()");
            wm.addView(getView(), params);
        }catch (Exception e){
            Log.e(TAG,"addView Exception: "+e.toString(), e);
        }

    }

    public void dismiss(){
        Log.d(TAG, "dismiss callFloatView : " + callFloatView);
        if(callFloatView != null){
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(WINDOW_SERVICE);
            try{
                wm.removeView(callFloatView);
                callFloatView = null;
            }catch (Exception e){
                Log.e(TAG,"removeView Exception: "+e.toString(), e);
            }
        }
    }

    public void registerCallFloatListener(IMeetingFloatViewListener listener){
        this.listener = listener;
    }

    public void unregisterCallFloatListener(){
        this.listener = null;
    }

    public void modifyCallFloat() {
        floatWinName.setText(context.getString(R.string.float_view_meeting_name));
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int startTouchX ;
        private int startTouchY ;
        private int beginX = 0;
        private int beginY = 0;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.d(TAG, "onTouch  event.getAction(): " + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startTouchX  = (int) event.getRawX();
                    startTouchY  = (int) event.getRawY();
                    beginX = startTouchX;
                    beginY = startTouchY;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - startTouchX ;
                    int movedY = nowY - startTouchY ;
                    Log.d(TAG, "  movedX: " + movedX + ", movedY: " + movedY);
                    startTouchX  = nowX;
                    startTouchY  = nowY;
                    params.x = params.x + movedX;
                    params.y = params.y + movedY;

                    if(params.x + callFloatView.getWidth() > screenWidth){
                        params.x = screenWidth - callFloatView.getWidth();
                    }
                    if(params.x < 0){
                        params.x = 0;
                    }
                    if(params.y + callFloatView.getHeight() > screenHeight){
                        params.y = screenHeight - callFloatView.getHeight();
                    }
                    if(params.y < 0){
                        params.y = 0;
                    }
                    if (Math.abs(movedX) > 5 || Math.abs(movedY) > 5){
                        isMove = true;
                    }
                    if(isMove) {
                        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(WINDOW_SERVICE);
                        wm.updateViewLayout(view, params);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "ACTION_UP isMove : " + isMove);
                    if (!isMove) { //click
                        Log.d(TAG, "ACTION_UP click " );
                        listener.onClickMeetingFloatView();
                    }
                    isMove = false;
                    return true;
                default:
                    break;
            }
            return false;
        }
    }
}
