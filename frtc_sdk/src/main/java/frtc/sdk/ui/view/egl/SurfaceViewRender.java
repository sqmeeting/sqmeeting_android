package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import frtc.sdk.ui.view.MediaTextureView;

public class SurfaceViewRender implements ISurfaceViewRender {

    private MediaTextureView mediaTextureView;
    private final frtc.sdk.internal.jni.Renderer jniRender = new frtc.sdk.internal.jni.Renderer();
    private int viewWidth;
    private int viewHeight;

    public SurfaceViewRender(MediaTextureView mediaTextureView) {
        this.mediaTextureView = mediaTextureView;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mediaTextureView.getMsid() != null) {
            this.jniRender.render(mediaTextureView.getMsid(), viewWidth, viewHeight);
        }
    }

    @Override
    public void onSurfaceViewChanged(GL10 gl, int w, int h) {
        this.viewWidth = w;
        this.viewHeight = h;
    }

    @Override
    public void onSurfaceViewCreated(GL10 gl, EGLConfig config) {
        this.jniRender.start();
    }
}
