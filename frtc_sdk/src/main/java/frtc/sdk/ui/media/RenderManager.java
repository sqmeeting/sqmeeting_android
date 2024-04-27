package frtc.sdk.ui.media;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.List;

import frtc.sdk.log.Log;

public class RenderManager {
    private final String TAG = RenderManager.class.getSimpleName();

    public static final long MEDIA_RENDER_FPS = 30;

    private HandlerThread mediaRenderThread;
    private Handler mediaRenderHandler;
    private List<MediaModule> peopleList;
    private List<MediaModule> contentList;
    private List<MediaModule> activeList;

    private Runnable mediaRenderRunnable;

    public RenderManager(List<MediaModule> peopleList, List<MediaModule> contentList, List<MediaModule> activeList){
        this.peopleList = peopleList;
        this.contentList = contentList;
        this.activeList = activeList;
    }

    private void initMediaRenderRunnable(){
        mediaRenderRunnable = new Runnable() {
            @Override
            public void run() {
                if (peopleList != null) {
                    for (MediaModule peopleCell : peopleList) {
                        if(!peopleCell.isEnabledViewRender()){
                            peopleCell.reqViewRender();
                        }
                    }
                }

                if (contentList != null) {
                    for (MediaModule contentCell : contentList) {
                        if(!contentCell.isEnabledViewRender()){
                            contentCell.reqViewRender();
                        }
                    }
                }

                if (activeList != null) {
                    for (MediaModule activeCell : activeList) {
                        if(!activeCell.isEnabledViewRender()){
                            activeCell.reqViewRender();
                        }
                    }
                }
                mediaRenderHandler.postDelayed(this, 1000 / MEDIA_RENDER_FPS );
            }
        };
    }

    public void init(){
        initMediaRenderRunnable();
        startMediaRenderThread();
    }

    public void destroy(){
        stopMediaRenderThread();
        peopleList = null;
        contentList = null;
        activeList = null;
    }

    public void startRender(){
        if(mediaRenderHandler != null && mediaRenderRunnable != null){
            mediaRenderHandler.post(mediaRenderRunnable);
        }
    }

    public void stopRender(){
        if(mediaRenderHandler != null && mediaRenderRunnable != null){
            mediaRenderHandler.removeCallbacks(mediaRenderRunnable);
        }
    }


    private void startMediaRenderThread() {
        if(mediaRenderThread == null && mediaRenderHandler == null){
            mediaRenderThread = new HandlerThread("MediaRenderThread");
            mediaRenderThread.start();
            mediaRenderHandler = new Handler(mediaRenderThread.getLooper());
        }
    }

    private void stopMediaRenderThread() {
        if (mediaRenderThread != null) {
            mediaRenderThread.quitSafely();
            try {
                mediaRenderThread.join();
                if (mediaRenderHandler != null) {
                    mediaRenderHandler.removeCallbacks(null);
                    mediaRenderHandler = null;
                }
                mediaRenderThread = null;

            } catch (InterruptedException e) {
                Log.e(TAG,"stopMediaRenderThread:"+e);
            }
        }
    }
}
