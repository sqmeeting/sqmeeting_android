package frtc.sdk.ui.device;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import frtc.sdk.log.Log;
import frtc.sdk.ui.model.CameraErrorType;
import frtc.sdk.ui.model.CameraMessageType;
import frtc.sdk.ui.model.CameraState;
import frtc.sdk.util.CameraSizeComparator;
import frtc.sdk.model.CameraConstant;

public class CameraMessageHandler extends Handler {
    private final String TAG = CameraMessageHandler.class.getSimpleName();
    private Context context = null;
    protected volatile CameraState curCameraState = CameraState.CLOSED;
    private volatile boolean enabledCamera = false;

    private int curCameraId = -1;
    private int fCameraId = -1;
    private int bCameraId = -1;

    private ICameraStateListener stateListener;
    private Camera curCamera;
    private Camera.Parameters[] cameraParams;
    private byte[][] bfs;
    private final Point capturePoint = new Point();

    public CameraMessageHandler(Context ctx, Looper lp, int cid) {
        super(lp);
        this.context = ctx;
        this.curCameraId = cid;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
           if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                fCameraId = i;
                curCameraId = i;
            } else if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                bCameraId = i;
                curCameraId = i;
            }
        }
        if (curCameraId >= 0) {
            bfs = new byte[CameraConstant.bufferSize][];
            cameraParams = new Camera.Parameters[Camera.getNumberOfCameras()];
            enabledCamera = true;
        }
    }

    public void stop() {
        try {
            if (stateListener != null) {
                stateListener.onCameraClosed(curCameraId);
            }
            context = null;
            curCameraId = -1;
            stateListener = null;
            removeMessages(CameraMessageType.Open.getCode());
            obtainMessage(CameraMessageType.Close.getCode()).sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "stop: " + e.getMessage());
        }
    }


    public void setCameraStateListener(ICameraStateListener listener) {
        if(stateListener == null) {
            stateListener = listener;
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (message == null) {
            return;
        }
        if (enabledCamera) {
            CameraMessageType type = CameraMessageType.from(message.what);
            if (type == null) {
                return;
            }
            switch (type) {
                case Open:
                    openCamera();
                    break;
                case Close:
                    if (curCameraState == CameraState.OPENED) {
                        closeCamera();
                    }
                    break;
                case StartPreview:
                    startPreview(message);
                    break;
                case StopPreview:
                    stopCameraPreview();
                    break;
                case StartRecord:
                    startRecord(message);
                    break;
                case StopRecord:
                    stopRecord();
                    break;
                case SwitchCamera:
                    switchCamera();
                    break;
                case Rotation:
                    setCameraRotation();
                    break;
                default:
                    break;
            }
        }
    }

    private void setCameraRotation() {
        if (curCamera != null) {
            setCameraOrientation((Activity) context, curCameraId, curCamera);
        }
    }

    private void switchCamera() {
        try {
            if (Camera.getNumberOfCameras() <= 1) {
                return;
            }
            closeCamera();
            if (curCameraId == bCameraId && fCameraId >= 0) {
                curCameraId = fCameraId;
            } else if (curCameraId == fCameraId && bCameraId >= 0) {
                curCameraId = bCameraId;
            }
            openCamera();
        } catch (Exception e) {
            Log.e(TAG, "switchCamera: " + e.getMessage());
        }
    }

    private Camera.Size getPreferedCameraSize(Camera camera, List<Camera.Size> supportedSize) {
        List<Camera.Size> candidateList = new ArrayList<>();
        for (Camera.Size size : supportedSize) {
            if (CameraConstant.DEFAULT_POINT.x * CameraConstant.DEFAULT_POINT.y >= size.width * size.height) {
                float whRatio = (float) size.width / (float) size.height;
                float defaultRatio = (float) CameraConstant.DEFAULT_POINT.x / (float) CameraConstant.DEFAULT_POINT.y;
                if (whRatio == defaultRatio) {
                    candidateList.add(size);
                }
            }
        }
        if (!candidateList.isEmpty()) {
            int total = candidateList.size();
            candidateList.sort(new CameraSizeComparator());
            return candidateList.get(total - 1);
        }
        if (camera != null) {
            supportedSize.sort(new CameraSizeComparator());
            int result = Collections.binarySearch(supportedSize,
                    camera.new Size(CameraConstant.DEFAULT_POINT.x, CameraConstant.DEFAULT_POINT.y), new CameraSizeComparator());
            int idx = Math.abs(result);
            if (idx >=  supportedSize.size()) {
                idx =  supportedSize.size() - 1;
            }
            return supportedSize.get(idx);
        }
        return null;
    }


    private void openCamera() {
        try {
            if (curCameraState == CameraState.CLOSED) {
                if (curCameraId < 0) {
                    if (fCameraId >= 0) {
                        curCameraId = fCameraId;
                    } else if (bCameraId >= 0) {
                        curCameraId = bCameraId;
                    }
                }
                curCamera = Camera.open(curCameraId);
                Parameters p = curCamera.getParameters();
                cameraParams[curCameraId] = p;
                curCameraState = CameraState.OPENED;
                curCamera.setErrorCallback((error, camera) -> {
                    curCameraState = CameraState.CLOSED;
                    if (stateListener != null) {
                        stateListener.onCameraError(curCameraId, CameraErrorType.FAILED);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "openCamera error"+e);
            curCameraState = CameraState.CLOSED;
            if (stateListener != null) {
                stateListener.onCameraError(curCameraId, CameraErrorType.FAILED);
            }
        }
    }

    private Parameters getParameters(Camera camera, Parameters p) {
        p.setPreviewFormat(ImageFormat.NV21);
        List<String> focusModes = p.getSupportedFocusModes();
        if (focusModes != null && !focusModes.isEmpty()) {
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else {
                p.setFocusMode(focusModes.get(0));
            }
        }
        p.set(CameraConstant.KEY_RECORDING_HINT, "true");
        p.set(CameraConstant.KEY_MODE, "video-mode");
        Camera.Size preferSize = getPreferedCameraSize(camera, p.getSupportedPreviewSizes());
        capturePoint.x = preferSize.width;
        capturePoint.y = preferSize.height;
        p.setPreviewSize(preferSize.width, preferSize.height);
        return p;
    }

    private void closeCamera() {
        try {
            if (curCamera != null) {
                curCamera.release();
                for (int i = 0; i < CameraConstant.bufferSize; i++) {
                    bfs[i] = null;
                }
                curCamera = null;
                curCameraState = CameraState.CLOSED;
                if (stateListener != null) {
                    stateListener.onCameraClosed(curCameraId);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "closeCamera: " + e.getMessage());
        }
    }

    private void setCameraOrientation(Activity activity, int cameraId, Camera camera) {
        if(cameraId == -1){
            return;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        int degrees = CameraManager.getRotation(activity);
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void startPreview(Message message) {
        try {
            if (curCameraState == CameraState.OPENED) {
                if (curCameraId >= 0 && curCamera != null) {
                    Parameters p = getParameters(curCamera, cameraParams[curCameraId]);
                    curCamera.setParameters(p);
                    setCameraOrientation((Activity) context, curCameraId, curCamera);
                }
                try {
                    if (!(message.obj instanceof SurfaceHolder)) {
                        return;
                    }
                    SurfaceHolder holder = (SurfaceHolder) message.obj;
                    curCamera.setPreviewDisplay(holder);
                    curCamera.startPreview();
                    curCameraState = CameraState.PREVIEW;
                    if (stateListener != null) {
                        stateListener.onCameraOpened(curCameraId);
                        stateListener.onCameraError(curCameraId, CameraErrorType.OPENED);
                    }
                } catch (Exception ex) {
                    curCameraState = CameraState.CLOSED;
                    if (stateListener != null) {
                        stateListener.onCameraError(curCameraId, CameraErrorType.FAILED);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "startPreview: " + e.getMessage());
        }
    }
    private void stopCameraPreview() {
        try {
            if (curCameraState == CameraState.PREVIEW) {
                if (curCamera != null) {
                    curCamera.stopPreview();
                    curCamera.setPreviewTexture(null);
                    curCamera.setPreviewDisplay(null);
                }
                curCameraState = CameraState.OPENED;
                if (stateListener != null) {
                    stateListener.onCameraClosed(curCameraId);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "stopCameraPreview: " + e.getMessage());
        }
    }

    private void startRecord(Message message) {
        try {
            if (curCameraState == CameraState.PREVIEW) {
                if (message.obj instanceof PreviewCallback) {
                    Parameters p = cameraParams[curCameraId];
                    int previewFormat = p.getPreviewFormat();
                    int length = capturePoint.x * capturePoint.y * ImageFormat.getBitsPerPixel(previewFormat) / 8;
                    for (int i = 0; i < CameraConstant.bufferSize; i++) {
                        bfs[i] = new byte[length];
                        curCamera.addCallbackBuffer(bfs[i]);
                    }
                    PreviewCallback cb = (PreviewCallback) message.obj;
                    curCamera.setPreviewCallbackWithBuffer(cb);
                    curCameraState = CameraState.RECORDING;
                    if (cb instanceof CameraMediaDataHandler) {
                        CameraMediaDataHandler ds = (CameraMediaDataHandler) cb;
                        ds.setCameraPreview(capturePoint.x, capturePoint.y);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "startRecord: " + e.getMessage());
        }
    }

    private void stopRecord() {
        try {
            if (curCameraState == CameraState.RECORDING) {
                if (curCamera != null) {
                    curCamera.setPreviewCallbackWithBuffer(null);
                }
                for (int i = 0; i < CameraConstant.bufferSize; i++) {
                    bfs[i] = null;
                }
                curCameraState = CameraState.PREVIEW;
            }
        } catch (Exception e) {
            Log.e(TAG, "stopRecord: " + e.getMessage());
        }
    }



}
