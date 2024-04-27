package frtc.sdk.ui.device;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;

import java.nio.ByteBuffer;

import frtc.sdk.internal.jni.Video;
import frtc.sdk.internal.model.VideoColorType;
import frtc.sdk.ui.media.IMediaDataHandler;

public class CameraMediaDataHandler implements PreviewCallback, IMediaDataHandler {
	public static final String TAG = CameraMediaDataHandler.class.getSimpleName();
	public int w;
	public int h;
	private volatile int rt = 0;
	protected String msid;

	public CameraMediaDataHandler(String msid) {
		super();
		this.msid = msid;
	}

	public void setMsid(String msid){
		this.msid = msid;
	}

	public void setCameraPreview(int width, int height) {
		this.w = width;
		this.h = height;
	}

	public void setRotation(int r) {
		this.rt = r;
	}

	@Override
	public void onPreviewFrame(byte[] array, Camera camera) {
		write(array, 0, rt);
		camera.addCallbackBuffer(array);
	}

	@Override
	public void writeBuffer(ByteBuffer buffer, int s, int r) {
		if (r > 0 || r == 0) {
			Video.writeBuffer(this.msid, buffer, w, h, r, VideoColorType.kNV21.getCode(), w);
		} else {
			Video.writeBuffer(this.msid, buffer, w, h, 0, VideoColorType.kNV21.getCode(), w);
		}
	}

	@Override
	public void write(byte[] array, int by, int r) {
		if (array != null) {
			if (r > 0 || r == 0) {
				Video.write(this.msid, array, array.length, w, h, r, by);
			} else {
				Video.write(this.msid, array, array.length, w, h, 0, by);
			}
		}
	}

}
