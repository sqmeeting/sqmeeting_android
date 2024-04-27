package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class ContextFactory implements IContextFactory {

    public EGLContext create(EGL10 gl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        int[] list = {0x3098, 2, EGL10.EGL_NONE};
        return gl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, list);
    }

    public void destroy(EGL10 gl, EGLDisplay eglDisplay, EGLContext eglContext) {
        gl.eglDestroyContext(eglDisplay, eglContext);
    }
}