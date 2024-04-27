package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public interface IContextFactory {
    EGLContext create(EGL10 gl, EGLDisplay eglDisplay, EGLConfig eglConfig);
    void destroy(EGL10 gl, EGLDisplay eglDisplay, EGLContext eglContext);
}
