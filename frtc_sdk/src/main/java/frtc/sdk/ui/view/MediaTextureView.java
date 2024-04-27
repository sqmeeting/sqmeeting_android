package frtc.sdk.ui.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnLayoutChangeListener;

import frtc.sdk.ui.view.egl.ContextFactory;
import frtc.sdk.ui.view.egl.DefaultContextFactory;
import frtc.sdk.ui.view.egl.DefaultSurfaceFactory;
import frtc.sdk.ui.view.egl.EGLConfigSelector;
import frtc.sdk.ui.view.egl.EGLThread;
import frtc.sdk.ui.view.egl.IConfigSelector;
import frtc.sdk.ui.view.egl.IContextFactory;
import frtc.sdk.ui.view.egl.ISurfaceFactory;
import frtc.sdk.ui.view.egl.ISurfaceViewRender;
import frtc.sdk.ui.view.egl.SurfaceViewRender;

public class MediaTextureView extends TextureView implements SurfaceTextureListener, OnLayoutChangeListener {
    private final static String TAG = MediaTextureView.class.getSimpleName();

    private EGLThread glThread;
    private ISurfaceViewRender viewRender;
    private volatile boolean detached;
    private IConfigSelector configSelector;
    private IContextFactory contextFactory;
    private ISurfaceFactory surfaceFactory;

    private volatile String msid;

    public MediaTextureView(Context context) {
        super(context);
        setSurfaceTextureListener(this);
        initialize();
    }
    public MediaTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
        initialize();
    }

    public MediaTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSurfaceTextureListener(this);
        initialize();
    }

    private void initialize() {
        if (glThread != null) {
            throw new IllegalStateException("setRenderer IllegalStateException");
        }
        contextFactory = new ContextFactory();
        setRenderer(new SurfaceViewRender(this));
        setMode(0);
    }

    public void setMsid(String msid) {
        this.msid = msid;
    }

    public String getMsid() {
        return this.msid;
    }

    public ISurfaceViewRender getRenderer() {
        return this.viewRender;
    }

    public IContextFactory getContextFactory() {
        return this.contextFactory;
    }

    public IConfigSelector getConfigSelector() {
        return this.configSelector;
    }
    public ISurfaceFactory getSurfaceFactory() {
        return this.surfaceFactory;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int w, int h) {
        glThread.surfaceCreated();
        glThread.resizeWindow(w, h);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int w, int h) {
        glThread.resizeWindow(w, h);
        surface.setDefaultBufferSize(w, h);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        glThread.destroySurface();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        glThread.doNotifyRender();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (glThread != null) {
                glThread.notifyAllAndWait();
            }
        } finally {
            super.finalize();
        }
    }

    public void setRenderer(ISurfaceViewRender viewRender) {
        if (glThread != null) {
            throw new IllegalStateException("setRenderer IllegalStateException");
        }
        if (configSelector == null) {
            configSelector = new EGLConfigSelector();
        }
        if (contextFactory == null) {
            contextFactory = new DefaultContextFactory();
        }
        if (surfaceFactory == null) {
            surfaceFactory = new DefaultSurfaceFactory();
        }
        this.viewRender = viewRender;
        glThread = new EGLThread(this);
        glThread.start();
    }

    public void setMode(int mode) {
        glThread.setMode(mode);
    }

    public void notifyRender() {
        glThread.doNotifyRender();
    }

    public void onResume() {
        glThread.doResume();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.detached && (this.viewRender != null)) {
            int renderMode = 1;
            if (glThread != null) {
                renderMode = glThread.getRender_mode();
            }
            glThread = new EGLThread(this);
            if (renderMode != 1) {
                glThread.setMode(renderMode);
            }
            glThread.start();
        }
        this.detached = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (glThread != null) {
            glThread.notifyAllAndWait();
        }
        this.detached = true;
        super.onDetachedFromWindow();
    }

    @Override
    public void onLayoutChange(View v, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
        onSurfaceTextureSizeChanged(getSurfaceTexture(), r - l, b - t);
    }

}
