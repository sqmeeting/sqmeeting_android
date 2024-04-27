package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

import frtc.sdk.log.Log;

public class DefaultContextFactory implements IContextFactory {

    private static final String TAG = DefaultSurfaceFactory.class.getSimpleName();

    public EGLContext create(EGL10 egl, EGLDisplay display, EGLConfig config) {
        EGLContext eglContext = null;
        try {
            eglContext = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, new int[]{0x3098, 2, EGL10.EGL_NONE });
        } catch (Exception e) {
            Log.e(TAG, "create eglContext failed" + e);
        }
        return eglContext;
    }

    public void destroy(EGL10 gl, EGLDisplay eglDisplay, EGLContext eglContext) {
        if (!gl.eglDestroyContext(eglDisplay, eglContext)) {
            throw new RuntimeException(TAG + "destroy failed" + gl.eglGetError());
        }
    }
}