package frtc.sdk.ui.media;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import frtc.sdk.ui.device.CameraCallback;
import frtc.sdk.ui.layout.ILayoutHandler;
import frtc.sdk.ui.view.MediaSurfaceView;
import frtc.sdk.util.AnimationBuilder;

public class LocalPeopleViewModule implements ILayoutHandler, View.OnTouchListener{

    private final String TAG = LocalPeopleViewModule.class.getSimpleName();
    protected MediaSurfaceView mediaView;
    protected AnimationBuilder animationBuilder = null;

    private volatile String uuid;
    private String msid;
    protected float scale;

    public LocalPeopleViewModule(Context context) {
        mediaView = new MediaSurfaceView(context);
        animationBuilder = new AnimationBuilder(mediaView);
        mediaView.getView().setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void doCreateAnimation(int w, int h, int mLeft, int mTop) {
        if (animationBuilder != null && !animationBuilder.isAnimationRunning()) {
            animationBuilder.clearData();
            animationBuilder.setViewWidth(w).setViewHeight(h).
                    setLeftMargin(mLeft).setTopMargin(mTop).setTranslationX(this.getView().getTranslationX()).
                    setTranslationY(this.getView().getTranslationY()).build().
                    startPositionAnimation().setDuration(0).start().resetAnimation().
                    buildScale().setDuration(100).start();
            invalidate();
        }
    }

    public void setActiveSpeaker(boolean active) {
        mediaView.setViewActiveSpeaker(active);
    }

    @Override
    public void doUpdateAnimation(int w, int h, int mLeft, int mTop) {
        if (animationBuilder != null && !animationBuilder.isAnimationRunning()) {
            animationBuilder.clearData();
            animationBuilder.setViewWidth(w).setViewHeight(h).
                    setLeftMargin(mLeft).setTopMargin(mTop).setTranslationX(this.getView().getTranslationX()).
                    setTranslationY(this.getView().getTranslationY()).buildTransition().
                    buildScale().setDuration(100).start();
            invalidate();
        }
    }

    @Override
    public void doLayoutAnimation(int w, int h, int mLeft, int mTop){
        if (animationBuilder != null) {
            if(animationBuilder.isAnimationRunning()){
                animationBuilder.cancel();
            }
            animationBuilder.resetData();
            mediaView.setTranslationX(0.0f);
            mediaView.setTranslationY(0.0f);
            animationBuilder.setViewWidth(w).setViewHeight(h).
                    setLeftMargin(mLeft).setTopMargin(mTop).setTranslationX(this.getView().getTranslationX()).
                    setTranslationY(this.getView().getTranslationY()).build().
                    startPositionAnimation().setDuration(0).start().resetAnimation().
                    buildScale().setDuration(100).start();
            invalidate();
        }
    }

    @Override
    public void setLocalView(int width, int height, int marginLeft, int marginTop) {
        if (animationBuilder != null && !animationBuilder.isAnimationRunning()) {
            animationBuilder.clearData();
            animationBuilder.setViewWidth(width).setViewHeight(height).
                    setLeftMargin(marginLeft).setTopMargin(marginTop).setTranslationX(this.getView().getTranslationX()).
                    setTranslationY(this.getView().getTranslationY()).buildTransition().
                    buildScale().setDuration(10).start();
            invalidate();
        }
    }

    @Override
    public boolean isRendered() {
        return animationBuilder != null && animationBuilder.isAnimationRendered();
    }

    @Override
    public void doHidden() {
        mediaView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        mediaView.setTranslationX(0.0f);
        mediaView.setTranslationY(0.0f);
        if (animationBuilder != null)
            animationBuilder.resetData();
    }

    @Override
    public boolean isRunning() {
        return animationBuilder != null && animationBuilder.isAnimationRunning();
    }

    @Override
    public void doScale(float scale) {
        this.scale = scale;
    }

    public View getView() {
        return mediaView;
    }

    public void setVideoCallback(CameraCallback cameraCallback) {
        mediaView.setCameraCallback(cameraCallback);
    }

    public void setLocalVideoMuted(boolean muted, int orientation) {
        mediaView.setVideoMuted(muted, orientation);
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMsid(){
        return this.msid;
    }
    public void setMsid(String msid){
        this.msid = msid;
    }

    public boolean isLayoutVisible() {
        return mediaView.isViewVisible();
    }

    public void setVisible(boolean visible) {
        mediaView.setViewVisible(visible);
    }

    public void setVisibility(int visibility) {
        if (mediaView != null)
            mediaView.setVisibility(visibility);
    }

    public void setDisplayName(String displayName) {
        mediaView.setName(displayName);
    }

    public void invalidate() {
        mediaView.invalidate();
    }

}
