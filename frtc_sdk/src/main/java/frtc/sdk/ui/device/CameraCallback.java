package frtc.sdk.ui.device;

import android.content.Context;
import android.view.SurfaceHolder;

public class CameraCallback implements SurfaceHolder.Callback {
    private final CameraManager cameraManager = CameraManager.getInstance();
    private boolean videoMuted;
    private volatile Object previewHolder;
    private int w;
    private int h;
    private CameraMediaDataHandler data = null;

    public CameraCallback(Context context, ICameraStateListener listener, int cameraId) {
        cameraManager.initialize(context, listener, cameraId);
    }

    public void releaseCamera() {
        cameraManager.stopRecord();
        cameraManager.stopCameraPreview();
        cameraManager.stopCamera();
        data = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        this.w = width;
        this.h = height;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        previewHolder = surfaceHolder;
        if(!videoMuted) {
            cameraManager.openCamera();
            startCameraPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        previewHolder = null;
    }

    public void setData(CameraMediaDataHandler data) {
        this.data = data;
    }

    public void closeCamera(){
        cameraManager.closeCamera();
    }

    public void openCamera(){
        cameraManager.openCamera();
    }

    public void startCameraPreview() {
        if (previewHolder != null) {
            cameraManager.startCameraPreview(previewHolder, this.w, this.h);
        }
    }
    public void resetCamera(Context context, ICameraStateListener listener, boolean videoMuted, int cameraId) {
        cameraManager.initialize(context, listener, cameraId);
        this.videoMuted = videoMuted;
        if(!videoMuted) {
            cameraManager.openCamera();
        }
    }
    public void setOrientation(int cameraRotation) {
        cameraManager.setOrientation(cameraRotation);
    }

    public void startCameraRecord() {
        if (data != null) {
            cameraManager.startRecord(data);
        }
    }

    public void stopCameraRecord() {
        cameraManager.stopRecord();
    }

    public void stopCameraPreview() {
        cameraManager.stopCameraPreview();
    }

    public void setMuteStatus(boolean muted){
       videoMuted = muted;
    }

    public void switchFrontOrBackCamera() {
        stopCameraRecord();
        cameraManager.switchCamera();
        startCameraPreview();
        startCameraRecord();
    }

}
