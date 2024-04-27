package frtc.sdk.ui.view.egl;

public class EGLThreadManager {
    private EGLThread eglThread;
    private volatile boolean version_flag;
    private volatile boolean driver_flag;
    private int version;

    public synchronized void verifyDriver() {
        if (!this.driver_flag) {
            verifyVersion();
            if (this.version < 0x20000) {
                notifyAll();
            }
            this.driver_flag = true;
        }
    }

    private void verifyVersion() {
        if (!this.version_flag) {
            this.version = 0x20000;
            this.version_flag = true;
        }
    }

    public synchronized void exit(EGLThread thread) {
        thread.setExited(true);
        if (eglThread == thread) {
            eglThread = null;
        }
        notifyAll();
    }

    public boolean tryAcquireContext(EGLThread thread) {
        if (eglThread == thread || eglThread == null) {
            eglThread = thread;
            notifyAll();
            return true;
        }
        verifyVersion();
        return true;
    }

    public synchronized boolean needTerminate() {
        verifyVersion();
        return false;
    }

    public void releaseContext(EGLThread thread) {
        if (eglThread == thread) {
            eglThread = null;
        }
        notifyAll();
    }

}
