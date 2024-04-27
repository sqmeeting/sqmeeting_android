package frtc.sdk.ui.device;

import android.app.Activity;
import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.view.Surface;

import frtc.sdk.log.Log;
import frtc.sdk.ui.model.CameraMessageType;

public class CameraManager {
    private final String TAG = this.getClass().getSimpleName();
    private static final CameraManager cameraManager = new CameraManager();
    private CameraMessageHandler handler;
    private HandlerThread handlerThread;
    public static CameraManager getInstance() {
        return cameraManager;
    }

    public synchronized void initialize(Context context, ICameraStateListener listener, int cameraId) {
        if (handlerThread == null ) {
            handlerThread = new HandlerThread("CameraManagerThread", Process.THREAD_PRIORITY_URGENT_DISPLAY);
            handlerThread.start();
            try {
                handler = new CameraMessageHandler(context, handlerThread.getLooper(), cameraId);
               if (listener != null){
                   handler.setCameraStateListener(listener);
               }
            } catch (Exception e) {
                Log.e(TAG, "initialize: " + e.getMessage());
            }
        }
    }

    public void openCamera() {
        try {
            handler.removeMessages(CameraMessageType.Close.getCode());
            handler.obtainMessage(CameraMessageType.Open.getCode()).sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "openCamera: " + e.getMessage());
        }
    }

    public void closeCamera() {
        try {
            handler.removeMessages(CameraMessageType.Open.getCode());
            handler.obtainMessage(CameraMessageType.Close.getCode()).sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "closeCamera: " + e.getMessage());
        }
    }

    public void stopCamera() {
        try {
            if (handler != null) {
                handler.stop();
            }
            handler = null;
            if (handlerThread != null) {
                handlerThread.quitSafely();
                handlerThread.join();
                handlerThread = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "stop: " + e.getMessage());
        }
    }

    public void stopRecord() {
        try {
            handler.removeMessages(CameraMessageType.StartRecord.getCode());
            handler.obtainMessage(CameraMessageType.StopRecord.getCode()).sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "stopRecord: " + e.getMessage());
        }
    }
    public void startRecord(CameraMediaDataHandler data) {
        try {
            Message message = handler.obtainMessage(CameraMessageType.StartRecord.getCode());
            message.obj = data;
            message.sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "startRecord: " + e.getMessage());
        }
    }

    public void switchCamera() {
        try {
            Message message = handler.obtainMessage(CameraMessageType.SwitchCamera.getCode());
            message.sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "sendSwitchCameraMessage: " + e.getMessage());
        }
    }

    public void stopCameraPreview() {
        try {
            handler.removeMessages(CameraMessageType.StartPreview.getCode());
            handler.obtainMessage(CameraMessageType.StopPreview.getCode()).sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "stopCameraPreview: " + e.getMessage());
        }
    }
    public void startCameraPreview(Object display, int width, int height) {
        try {
            Message message = handler.obtainMessage(CameraMessageType.StartPreview.getCode());
            message.arg1 = width;
            message.arg2 = height;
            message.obj = display;
            message.sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "startCameraPreview: " + e.getMessage());
        }
    }

    public void setOrientation(int rotation) {
        try {
            Message message = handler.obtainMessage(CameraMessageType.Rotation.getCode());
            message.arg1 = rotation;
            message.sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "sendCameraOrientationMessage: " + e.getMessage());
        }
    }

    public static int getRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                break;
        }
        return 0;
    }

}
