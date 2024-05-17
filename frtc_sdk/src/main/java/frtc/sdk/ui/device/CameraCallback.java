package frtc.sdk.ui.device;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.view.Surface;
import android.view.SurfaceHolder;

import frtc.sdk.log.Log;

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

    public void clearSurface(){
        Log.d("CameraCallback","clearSurface");
        if(previewHolder != null){
            try{
                Surface surface = ((SurfaceHolder)previewHolder).getSurface();
                EGLDisplay display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
                int[] version = new int[2];
                EGL14.eglInitialize(display, version, 0, version, 1);
                int[] attribList = {
                        EGL14.EGL_RED_SIZE, 8,
                        EGL14.EGL_GREEN_SIZE, 8,
                        EGL14.EGL_BLUE_SIZE, 8,
                        EGL14.EGL_ALPHA_SIZE, 8,
                        EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                        EGL14.EGL_NONE, 0,
                        EGL14.EGL_NONE
                };
                EGLConfig[] configs = new EGLConfig[1];
                int[] numConfigs = new int[1];
                EGL14.eglChooseConfig(display, attribList, 0, configs, 0, configs.length, numConfigs, 0);

                EGLConfig config = configs[0];
                EGLContext context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, new int[]{
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                        EGL14.EGL_NONE
                }, 0);

                EGLSurface eglSurface = EGL14.eglCreateWindowSurface(display, config, surface,
                        new int[]{
                                EGL14.EGL_NONE
                        }, 0);

                EGL14.eglMakeCurrent(display, eglSurface, eglSurface, context);
                GLES20.glClearColor(0, 0, 0, 1);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                EGL14.eglSwapBuffers(display, eglSurface);
                EGL14.eglDestroySurface(display, eglSurface);
                EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(display, context);
                EGL14.eglTerminate(display);
            }catch (Exception exception){
                Log.e("CameraCallback","clearSurface failed",exception);
            }
        }
    }

}
