package frtc.sdk.internal.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import frtc.sdk.internal.ITask;

public class FrtcTaskPoolManager {
    private static FrtcTaskPoolManager instance;
    private static final int POOL_SIZE = 1;
    private static ExecutorService executorService = null;

    public static FrtcTaskPoolManager getInstance() {
        if (instance == null) {
            synchronized (FrtcTaskPoolManager.class) {
                if (instance == null) {
                    instance = new FrtcTaskPoolManager();
                }
            }
        }
        return instance;
    }

    public void submitTask(ITask task) {
        executorService.submit(task);
    }

    private FrtcTaskPoolManager() {
        executorService = Executors.newFixedThreadPool(POOL_SIZE);
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
