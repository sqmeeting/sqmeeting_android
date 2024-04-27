package frtc.sdk.ui.view.egl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.opengles.GL10;

import frtc.sdk.log.Log;
import frtc.sdk.ui.view.MediaTextureView;

public class EGLThread extends Thread {
    private static final String TAG = EGLThread.class.getSimpleName();
    private static final EGLThreadManager glThreadManager = new EGLThreadManager();
    private final List<Runnable> queue = new ArrayList<>();
    private int width;
    private int height;
    private int render_mode;
    private boolean render_req;
    private boolean render_finished;
    private boolean need_release;
    private volatile boolean exited;
    private EGLManager manager;
    private MediaTextureView textureView;
    private boolean should_exit;
    private boolean req_paused;
    private boolean paused;
    private boolean finished_Creating;
    private boolean have_surface;
    private boolean exist_context;
    private boolean is_surface_bad;
    private boolean waiting_surface;
    private boolean exist_surface;
    private boolean size_changed = true;

    public EGLThread(MediaTextureView textureView) {
        super();
        width = 0;
        height = 0;
        render_req = true;
        render_mode = 1;
        this.textureView = textureView;
    }

    @Override
    public void run() {
        setName(TAG + " " + getId());
        try {
            doRun();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            glThreadManager.exit(this);
        }
    }

    private void stopSurface() {
        if (exist_surface) {
            exist_surface = false;
            manager.destroy();
        }
    }

    private void stopContext() {
        if (exist_context) {
            manager.finish();
            exist_context = false;
            glThreadManager.releaseContext(this);
        }
    }
    private void doRun() throws InterruptedException {
        manager = new EGLManager(this.textureView);
        exist_context = false;
        exist_surface = false;
        try {
            GL10 gl = null;
            boolean create_context = false;
            boolean create_surface = false;
            boolean create_interface = false;
            boolean lost_context = false;
            boolean sizeChanged = false;
            boolean render_notification = false;
            boolean render_notify = false;
            boolean release_context = false;
            int w = 0;
            int h = 0;
            Runnable runnable = null;
            while (true) {
                synchronized (glThreadManager) {
                    while (true) {
                        if (should_exit) {
                            return;
                        }
                        if (!queue.isEmpty()) {
                            runnable = queue.remove(0);
                            break;
                        }
                        boolean pausing = false;
                        if (paused != req_paused) {
                            pausing = req_paused;
                            paused = req_paused;
                            glThreadManager.notifyAll();
                        }
                        if (need_release) {
                            stopSurface();
                            stopContext();
                            need_release = false;
                            release_context = true;
                        }
                        if (lost_context) {
                            stopSurface();
                            stopContext();
                            lost_context = false;
                        }
                        if (pausing && exist_surface) {
                            stopSurface();
                        }
                        if (pausing) {
                            if (glThreadManager.needTerminate()) {
                                manager.finish();
                            }
                        }
                        if ((!have_surface) && (!waiting_surface)) {
                            if (exist_surface) {
                                stopSurface();
                            }
                            waiting_surface = true;
                            is_surface_bad = false;
                            glThreadManager.notifyAll();
                        }

                        if (have_surface && waiting_surface) {
                            waiting_surface = false;
                            glThreadManager.notifyAll();
                        }
                        if (render_notify) {
                            render_notification = false;
                            render_notify = false;
                            render_finished = true;
                            glThreadManager.notifyAll();
                        }
                        if (isReady()) {
                            if (!exist_context) {
                                if (release_context) {
                                    release_context = false;
                                } else if (glThreadManager.tryAcquireContext(this)) {
                                    try {
                                        manager.start();
                                    } catch (RuntimeException t) {
                                        glThreadManager.releaseContext(this);
                                        throw t;
                                    }
                                    exist_context = true;
                                    create_context = true;
                                    glThreadManager.notifyAll();
                                }
                            }
                            if (exist_context && !exist_surface) {
                                exist_surface = true;
                                create_surface = true;
                                create_interface = true;
                                sizeChanged = true;
                            }
                            if (exist_surface) {
                                if (this.size_changed) {
                                    sizeChanged = true;
                                    w = width;
                                    h = height;
                                    render_notification = true;
                                    create_surface = true;
                                    this.size_changed = false;
                                }
                                render_req = false;
                                glThreadManager.notifyAll();
                                break;
                            }
                        }
                        glThreadManager.wait();
                    }
                }
                if (runnable != null) {
                    runnable.run();
                    runnable = null;
                    continue;
                }
                if (create_surface) {
                    if (manager.createSurface()) {
                        synchronized(glThreadManager) {
                            finished_Creating = true;
                            glThreadManager.notifyAll();
                        }
                    } else {
                        synchronized(glThreadManager) {
                            finished_Creating = true;
                            is_surface_bad = true;
                            glThreadManager.notifyAll();
                        }
                        continue;
                    }
                    create_surface = false;
                }
                if (create_interface) {
                    gl = (GL10) manager.create();
                    glThreadManager.verifyDriver();
                    create_interface = false;
                }
                if (create_context) {
                    if (textureView != null) {
                        textureView.getRenderer().onSurfaceViewCreated(gl, manager.getConfig());
                    }
                    create_context = false;
                }
                if (sizeChanged) {
                    if (textureView != null) {
                        textureView.getRenderer().onSurfaceViewChanged(gl, w, h);
                    }
                    sizeChanged = false;
                }
                if (textureView != null) {
                    textureView.getRenderer().onDrawFrame(gl);
                }
                int swap_error = manager.swapBuffers();
                switch (swap_error) {
                    case EGL10.EGL_SUCCESS:
                        break;
                    case EGL11.EGL_CONTEXT_LOST:
                        lost_context = true;
                        break;
                    default:
                        Log.w(TAG, "swapBuffers error: " + swap_error);
                        synchronized(glThreadManager) {
                            is_surface_bad = true;
                            glThreadManager.notifyAll();
                        }
                        break;
                }
                if (render_notification) {
                    render_notify = true;
                }
            }
        } finally {
            synchronized (glThreadManager) {
                stopSurface();
                stopContext();
            }
        }
    }

    private boolean isReady() {
        return (width > 0) && (height > 0) && have_surface && (!is_surface_bad) && (!paused)
                && (render_req || (render_mode == 1));
    }

    public void setMode(int mode) {
        if ( !((0 <= mode) && (mode <= 1)) ) {
            throw new IllegalArgumentException("render mode");
        }
        synchronized(glThreadManager) {
            render_mode = mode;
            glThreadManager.notifyAll();
        }
    }

    public boolean canDraw() {
        return exist_context && exist_surface && isReady();
    }

    public int getRender_mode() {
        synchronized(glThreadManager) {
            return render_mode;
        }
    }

    public void doNotifyRender() {
        synchronized(glThreadManager) {
            render_req = true;
            glThreadManager.notifyAll();
        }
    }

    public void surfaceCreated() {
        synchronized(glThreadManager) {
            have_surface = true;
            finished_Creating = false;
            glThreadManager.notifyAll();
            while (waiting_surface
                    && !finished_Creating
                    && !exited) {
                try {
                    glThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void destroySurface() {
        synchronized(glThreadManager) {
            have_surface = false;
            glThreadManager.notifyAll();
            while((!waiting_surface) && (!exited)) {
                try {
                    glThreadManager.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void notifyReleaseContext() {
        need_release = true;
        glThreadManager.notifyAll();
    }

    public void setExited(boolean exit) {
        this.exited = exit;
    }

    public void doResume() {
        synchronized (glThreadManager) {
            req_paused = false;
            render_req = true;
            render_finished = false;
            glThreadManager.notifyAll();
            while ((!exited) && paused && (!render_finished)) {
                try {
                    glThreadManager.wait();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void resizeWindow(int w, int h) {
        synchronized (glThreadManager) {
            width = w;
            height = h;
            size_changed = true;
            render_req = true;
            render_finished = false;
            glThreadManager.notifyAll();
            while (!exited && !paused && !render_finished && canDraw()) {
                try {
                    glThreadManager.wait();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void notifyAllAndWait() {
        synchronized(glThreadManager) {
            should_exit = true;
            glThreadManager.notifyAll();
            while (!exited) {
                try {
                    glThreadManager.wait();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}