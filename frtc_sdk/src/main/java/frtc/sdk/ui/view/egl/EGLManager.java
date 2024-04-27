package frtc.sdk.ui.view.egl;

import android.opengl.GLDebugHelper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

import frtc.sdk.log.Log;
import frtc.sdk.ui.view.MediaTextureView;

public class EGLManager {
    private static final String TAG = EGLManager.class.getSimpleName();
    private MediaTextureView glTextureView;
    private EGL10 egl;
    private EGLDisplay display;
    private EGLSurface surface;
    private EGLConfig config;
    private EGLContext context;
    public EGLManager(MediaTextureView textureView) {
        this.glTextureView = textureView;
    }

    public GL create() {
        GL gl = context.getGL();
        if (glTextureView != null) {
            int configFlags = 0;
            configFlags |= GLDebugHelper.CONFIG_CHECK_GL_ERROR;
            gl = GLDebugHelper.wrap(gl, configFlags, null);
        }
        return gl;
    }

    public void start() {
        egl = (EGL10) EGLContext.getEGL();
        display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (display == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("start failed");
        }
        int[] version = new int[2];
        if(!egl.eglInitialize(display, version)) {
            throw new RuntimeException("initialize failed");
        }
        if (glTextureView == null) {
            config = null;
            context = null;
        } else {
            config = glTextureView.getConfigSelector().select(egl, display);
            context = glTextureView.getContextFactory().create(egl, display, config);
        }
        if (context == null || context == EGL10.EGL_NO_CONTEXT) {
            context = null;
            throw new RuntimeException(TAG + " createContext failed");
        }
        surface = null;
    }

    public EGLConfig getConfig() {
        return config;
    }

    private void destroySurface() {
        if (surface != null && surface != EGL10.EGL_NO_SURFACE) {
            egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            if (glTextureView != null) {
                glTextureView.getSurfaceFactory().destroySurface(egl, display, surface);
            }
            surface = null;
        }
    }

    public boolean createSurface() {
        if (egl == null) {
            throw new RuntimeException("egl not initialized");
        }
        if (display == null) {
            throw new RuntimeException("eglDisplay not initialized");
        }
        if (config == null) {
            throw new RuntimeException("not initialized");
        }
        destroySurface();
        if (glTextureView != null) {
            surface = glTextureView.getSurfaceFactory()
                    .createWindowSurface(egl, display, config, glTextureView.getSurfaceTexture());
        } else {
            surface = null;
        }
        if (surface == null || surface == EGL10.EGL_NO_SURFACE) {
            int error = egl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                Log.e(TAG, "createWindowSurface.");
            }
            return false;
        }
        if (!egl.eglMakeCurrent(display, surface, surface, context)) {
            Log.w(TAG, "eglMakeCurrent failed" + egl.eglGetError());
            return false;
        }
        return true;
    }
    public void finish() {
        if (context != null) {
            if (glTextureView != null) {
                glTextureView.getContextFactory().destroy(egl, display, context);
            }
            context = null;
        }
        if (display != null) {
            egl.eglTerminate(display);
            display = null;
        }
    }

    public void destroy() {
        destroySurface();
    }

    public int swapBuffers() {
        if (!egl.eglSwapBuffers(display, surface)) {
            return egl.eglGetError();
        }
        return EGL10.EGL_SUCCESS;
    }

}
