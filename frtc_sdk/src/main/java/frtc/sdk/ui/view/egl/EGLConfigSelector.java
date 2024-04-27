package frtc.sdk.ui.view.egl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class EGLConfigSelector implements IConfigSelector {
    private int[] configs;
    private int[] value = new int[1];
    public EGLConfigSelector() {
        configs = filter(new int[] { EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8, EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 0, EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE});
    }
    private int fetchAttribute(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig, int attribute) {
        if (egl.eglGetConfigAttrib(eglDisplay, eglConfig, attribute, this.value)) {
            return this.value[0];
        }
        return 0;
    }
    public EGLConfig select(EGL10 egl, EGLDisplay eglDisplay) {
        int[] configs = new int[1];
        if (!egl.eglChooseConfig(eglDisplay, this.configs, null, 0, configs)) {
            throw new IllegalArgumentException("eglChooseConfig");
        }
        int num = configs[0];
        if (num <= 0) {
            throw new IllegalArgumentException("no match configs");
        }
        EGLConfig[] glConfig = new EGLConfig[num];
        if (!egl.eglChooseConfig(eglDisplay, this.configs, glConfig, num, configs)) {
            throw new IllegalArgumentException("eglChooseConfig");
        }
        EGLConfig cfg = select(egl, eglDisplay, glConfig);
        if (cfg == null) {
            throw new IllegalArgumentException("select config");
        }
        return cfg;
    }
    private EGLConfig select(EGL10 gl, EGLDisplay eglDisplay, EGLConfig[] configArray) {
        for (EGLConfig cfg : configArray) {
            if (fetchAttribute(gl, eglDisplay, cfg, EGL10.EGL_STENCIL_SIZE) >= 0
                    && fetchAttribute(gl, eglDisplay, cfg, EGL10.EGL_DEPTH_SIZE) >= 16) {
                int r = fetchAttribute(gl, eglDisplay, cfg, EGL10.EGL_RED_SIZE);
                int g = fetchAttribute(gl, eglDisplay, cfg, EGL10.EGL_GREEN_SIZE);
                int b = fetchAttribute(gl, eglDisplay, cfg, EGL10.EGL_BLUE_SIZE);
                int a = fetchAttribute(gl, eglDisplay, cfg, EGL10.EGL_ALPHA_SIZE);
                if ((r == 8) && (g == 8) && (b == 8) && (a == 0)) {
                    return cfg;
                }
            }
        }
        return null;
    }

    private int[] filter(int[] cfgs) {
        int len = cfgs.length;
        int[] newCfgs = new int[len + 2];
        System.arraycopy(cfgs, 0, newCfgs, 0, len - 1);
        newCfgs[len - 1] = EGL10.EGL_RENDERABLE_TYPE;
        newCfgs[len] = 4;
        newCfgs[len + 1] = EGL10.EGL_NONE;
        return newCfgs;
    }

}