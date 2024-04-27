package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import frtc.sdk.log.Log;

public class DefaultSurfaceFactory implements ISurfaceFactory {
    private static final String TAG = DefaultSurfaceFactory.class.getSimpleName();

    public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object native_window) {
        EGLSurface result = null;
        try {
            result = egl.eglCreateWindowSurface(display, config, native_window, null);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException"+e);
        }
        return result;
    }

    public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
        egl.eglDestroySurface(display, surface);
    }
}