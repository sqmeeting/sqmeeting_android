package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public interface ISurfaceFactory {
    EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config,
                                   Object nativeWindow);
    void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface);
}
