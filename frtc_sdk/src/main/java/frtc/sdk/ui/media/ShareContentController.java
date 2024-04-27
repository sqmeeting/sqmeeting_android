package frtc.sdk.ui.media;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import frtc.sdk.log.Log;
import frtc.sdk.ui.device.ContentMediaDataHandler;

public class ShareContentController {

    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ShareContentStatusListener listener;
    private ContentMediaDataHandler contentMediaDataHandler;

    private int displayWidth;
    private int displayHeight;
    private int density;
    private int displayRotation;

    private int screenWidth;
    private int screenHeight;

    private HandlerThread mHandlerThread;
    private Handler handler;

    private Thread cpuMonitorThread;
    private boolean cpuMonitored = false;

    private boolean isCapturing = false;

    public boolean getCaptureStatus(){
        return this.isCapturing;
    }

    private boolean contentSendRefused = false;

    private boolean isSQMeetingSelfStop = false;

    public ShareContentController(Context context) {
        this.context = context;
    }

    public interface ShareContentStatusListener{
        void onStopped();
        void onRefused();
        void toStop();
    }

    public void setShareContentStatusListener(ShareContentStatusListener listener){
        this.listener = listener;
    }

    public void sendStopContentCmd(){
        if(listener != null){
            listener.toStop();
        }
    }

    public MediaProjectionManager getMediaProjectionManager(){
        Log.d(TAG,"getMediaProjectionManager");
        if(mediaProjectionManager == null){
            mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        return mediaProjectionManager;
    }

    public void createMediaProduction(final int resultCode, @Nullable final Intent data){
        Log.d(TAG,"createMediaProduction");

        contentSendRefused = false;
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,data);
        mediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
                Log.d(TAG,"mediaProjection onStop()"+virtualDisplay+","+mediaProjection);
                if(listener != null){
                    if(contentSendRefused){
                        listener.onRefused();
                    }else{
                        if(isSQMeetingSelfStop) {
                            isSQMeetingSelfStop = false;
                        }else {
                            listener.toStop();
                        }
                    }
                }
            }},null);
    }

    public void updateContentSendRefusedStatus(boolean refused){
        Log.d(TAG,"updateContentSendRefusedStatus:"+refused);
        contentSendRefused = refused;
    }

    public boolean hasProjection(){
        return mediaProjection != null;
    }

    public void stopMediaProjection(){
        Log.d(TAG,"stopMediaProjection:"+mediaProjection);

        stopVirtualDisplay();
        if(mediaProjection != null){
            isSQMeetingSelfStop = true;
            mediaProjection.stop();
            mediaProjection = null;
            if(listener != null) {
                listener.onStopped();
            }
        }
    }

    public void stopVirtualDisplay(){

        Log.d(TAG,"stopVirtualDisplay:"+virtualDisplay);
        if(virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        stopContentControlThread();
        isCapturing = false;
    }

    public void createVirtualDisplay(final ContentMediaDataHandler contentMediaDataHandler){
        Log.d(TAG,"createVirtualDisplay:"+mediaProjection+","+virtualDisplay+","+ contentMediaDataHandler);
        isCapturing = true;
        startContentControlThread();
        if(cpuMonitored){
            startCpuMonitorThread();
        }

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        int screenDensity = displayMetrics.densityDpi;
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int orientation = windowManager.getDefaultDisplay().getRotation();

        if(screenWidth > screenHeight){
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }else{
            this.screenWidth = screenHeight;
            this.screenHeight = screenWidth;
        }

        density = screenDensity;
        displayRotation = orientation;

        if(displayRotation == 0 || displayRotation == 2){
            displayWidth = this.screenHeight;
            displayHeight = this.screenWidth;
        }else if(displayRotation == 1 || displayRotation == 3){
            displayWidth = this.screenWidth;
            displayHeight = this.screenHeight;
        }

        if(contentMediaDataHandler != null && mediaProjection != null && handler != null){

            this.contentMediaDataHandler = contentMediaDataHandler;
            Log.i(TAG,"createVirtualDisplay:width="+displayWidth+",height="+displayHeight+",screenDensity=" + density + ",orientation=" + orientation);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        virtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
                                displayWidth, displayHeight, density,
                                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                                contentMediaDataHandler.getImageReader(displayWidth,displayHeight).getSurface(),
                            null, null);
                    }catch (Exception e){
                        Log.e(TAG,"createVirtualDisplay Exception: "+e.toString(), e);
                    }

                }
            });
        }
    }

    private void startCpuMonitorThread(){
        Log.d(TAG,"startCpuMonitorThread:" + (cpuMonitorThread == null ? null : cpuMonitorThread.getState()));
        cpuMonitorThread = new Thread(new CpuMonitorLoop(),"Cpu-Monitor");
        cpuMonitorThread.start();
    }

    private class CpuMonitorLoop implements Runnable{
        @Override
        public void run() {
            Log.d(TAG,"CpuMonitorLoop start:");
            while(isCapturing){
                try{
                    int pid = 0;

                    ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                    for (ActivityManager.RunningAppProcessInfo runprocessInfo : appProcesses) {
                        pid = runprocessInfo.pid;
                        Log.d(TAG, "CPU uses " + getCpuUsage(pid) + "%");
                    }
                    Thread.sleep(10000);
                }catch (InterruptedException e){
                    Log.e(TAG,"CpuMonitorLoop can not sleep:"+e);
                }
            }
        }
    }

        private int CLK_TCK = 100;
        private long lastUsedCPUTime = 0L;
        private long lastRecordCPUTime = SystemClock.uptimeMillis();

        private float getCpuUsage(int pid) {
            File pidStatFile = new File("/proc/" + pid + "/stat");
            try (FileInputStream inputStream = new FileInputStream(pidStatFile);
                 InputStreamReader reader = new InputStreamReader(inputStream);
                 BufferedReader bufferedReader = new BufferedReader(reader)
            ) {
                String line = bufferedReader.readLine();
                String[] res = line.split(" ");
                long uTime = Long.parseLong(res[13]);
                long sTime = Long.parseLong(res[14]);
                long usedTime = (uTime + sTime) - lastUsedCPUTime;
                long currentTime = SystemClock.uptimeMillis();
                float elapsedTime = (currentTime - lastRecordCPUTime) / 1000f * CLK_TCK;

                lastUsedCPUTime = uTime + sTime;
                lastRecordCPUTime = currentTime;

                float usage = usedTime / elapsedTime * 100;
                return usage / Runtime.getRuntime().availableProcessors();
            } catch (Exception e) {
                Log.e(TAG,"getCpuUsage",e);
            }
            return 0f;
        }


    public void setRotation(int rotation){
        if(displayRotation != rotation){

            if(hasProjection() && virtualDisplay != null && contentMediaDataHandler != null) {
                displayRotation = rotation;
                if (displayRotation == 0 || displayRotation == 2) {
                    displayWidth = screenHeight;
                    displayHeight = screenWidth;
                } else if (displayRotation == 1 || displayRotation == 3) {
                    displayWidth = screenWidth;
                    displayHeight = screenHeight;
                }

                Log.d(TAG,"setRotation:"+ displayRotation);
                virtualDisplay.setSurface(null);
                contentMediaDataHandler.discard();
                virtualDisplay.resize(displayWidth, displayHeight, density);
                contentMediaDataHandler.start();
                virtualDisplay.setSurface(contentMediaDataHandler.getImageReader(displayWidth, displayHeight).getSurface());
                Log.d(TAG, "setRotation:" + displayWidth + "," + displayHeight + "," + density);
            }
        }
    }

    public void startContentControlThread() {
        Log.d(TAG,"startContentControlThread");
        if (mHandlerThread == null && handler == null) {
            mHandlerThread = new HandlerThread("call-thread");
            mHandlerThread.start();
            handler = new Handler(mHandlerThread.getLooper());
        }
    }

    public void stopContentControlThread() {
        if(mHandlerThread != null) {
            mHandlerThread.quit();
            try {
                mHandlerThread.join();
                mHandlerThread = null;
                handler = null;
            } catch (InterruptedException e) {
                Log.e(TAG,"stopContentControlThread failed",e);
            }
        }
    }

    public void setIsSQMeetingSelfStop(boolean isSelf) {
        isSQMeetingSelfStop = isSelf;
    }

}
