package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public interface ISurfaceViewRender {
    void onDrawFrame(GL10 gl);
    void onSurfaceViewCreated(GL10 gl, EGLConfig config);
    void onSurfaceViewChanged(GL10 gl, int w, int h);
}
