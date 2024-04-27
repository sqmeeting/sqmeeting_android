package frtc.sdk.ui.device;

import frtc.sdk.ui.model.CameraErrorType;

public interface ICameraStateListener {
	void onCameraError(int cid, CameraErrorType type);
	void onCameraOpened(int cid);
	void onCameraClosed(int cid);
}