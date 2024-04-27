package frtc.sdk.ui.media;

import android.animation.Animator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.concurrent.atomic.AtomicInteger;

import frtc.sdk.ui.layout.ILayoutHandler;
import frtc.sdk.ui.model.ViewConstant;
import frtc.sdk.ui.view.MediaFrameLayout;
import frtc.sdk.util.AnimationBuilder;
import frtc.sdk.ui.model.MediaType;

public class MediaModule implements ILayoutHandler, View.OnTouchListener, IRenderController {
    protected AnimationBuilder animationBuilder = null;
    private final String TAG = MediaModule.class.getSimpleName();
    protected AtomicInteger num = new AtomicInteger(0);
    private int oWidth;
    private int nWidth;

    private MediaType mediaType = MediaType.UNKNOWN;

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    protected MediaFrameLayout mediaViewController;
    private Animator.AnimatorListener aListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mediaViewController.resizeDisplayNameTextView(oWidth, nWidth);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public MediaModule(Context context) {
        mediaViewController = new MediaFrameLayout(context);
        animationBuilder = new AnimationBuilder(mediaViewController.getMediaView());
        mediaViewController.getMediaView().setOnTouchListener(this);
    }

    public MediaModule(){

    }

    public void onClick(MediaModule cell){

    }

    public void destroy(ViewGroup parent) {
        mediaViewController.destroy(parent);
        if (animationBuilder != null) {
            animationBuilder.resetAll();
            animationBuilder = null;
        }
        num.set(0);
    }

    @Override
    public void doCreateAnimation(int w, int h, int mLeft, int mTop) {
        if (animationBuilder != null && !animationBuilder.isAnimationRunning()) {
            animationBuilder.clearData();
            animationBuilder.setViewWidth(w).setViewHeight(h).
                    setLeftMargin(mLeft).setTopMargin(mTop).setTranslationX(this.getMediaView().getTranslationX()).
                    setTranslationY(this.getMediaView().getTranslationY()).build().
                    startPositionAnimation().setDuration(0).start().resetAnimation().
                    buildScale().setDuration(100).start();
            invalidate();
            oWidth = w;
        }
    }


    @Override
    public void doUpdateAnimation(int w, int h, int mLeft, int mTop) {
        if (animationBuilder != null && !animationBuilder.isAnimationRunning()) {
            animationBuilder.clearData();
            animationBuilder.setViewWidth(w).setViewHeight(h).
                    setLeftMargin(mLeft).setTopMargin(mTop).setTranslationX(this.getMediaView().getTranslationX()).
                    setTranslationY(this.getMediaView().getTranslationY()).buildTransition().
                    buildScale().setDuration(100).addListener(aListener).start();
            invalidate();
            this.nWidth = w;
        }
    }

    @Override
    public void doLayoutAnimation(int w, int h, int mLeft, int mTop){
        if (animationBuilder != null) {
            if(animationBuilder.isAnimationRunning()){
                animationBuilder.cancel();
            }
            animationBuilder.resetData();
            mediaViewController.getMediaView().setTranslationX(0.0f);
            mediaViewController.getMediaView().setTranslationY(0.0f);
            animationBuilder.setViewWidth(w).setViewHeight(h).
                    setLeftMargin(mLeft).setTopMargin(mTop).setTranslationX(this.getMediaView().getTranslationX()).
                    setTranslationY(this.getMediaView().getTranslationY()).build().
                    startPositionAnimation().setDuration(0).start().resetAnimation().
                    buildScale().setDuration(100).start();
            invalidate();
            this.oWidth = w;
        }
    }

    @Override
    public void setLocalView(int width, int height, int marginLeft, int marginTop) {
        if (animationBuilder != null && !animationBuilder.isAnimationRunning()) {
            animationBuilder.clearData();
            animationBuilder.setViewWidth(width).setViewHeight(height).
                    setLeftMargin(marginLeft).setTopMargin(marginTop).setTranslationX(this.getMediaView().getTranslationX()).
                    setTranslationY(this.getMediaView().getTranslationY()).buildTransition().
                    buildScale().setDuration(10).addListener(aListener).start();
            invalidate();
            this.nWidth = width;
        }
    }

    @Override
    public boolean isRendered() {
        return animationBuilder != null && animationBuilder.isAnimationRendered();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            this.getMediaView().postDelayed(() -> num.set(0), ViewConstant.post_delay_1s);
            if (num.incrementAndGet() == 2) {
                onClick(this);
            }
        }
        return false;
    }

    @Override
    public void doHidden() {
        mediaViewController.getMediaView().setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        mediaViewController.getMediaView().setTranslationX(0.0f);
        mediaViewController.getMediaView().setTranslationY(0.0f);
        if (animationBuilder != null){
            animationBuilder.resetData();
        }
    }

    @Override
    public boolean isRunning() {
        return animationBuilder != null && animationBuilder.isAnimationRunning();
    }

    @Override
    public void doScale(float scale) {
    }

    public View getMediaView() {
        return mediaViewController.getMediaView();
    }


    public void setMsid(String msid) {
        mediaViewController.setMsid(msid);
    }

    public String getMsid() {
        return mediaViewController.getMsid();
    }

    public void setUuid(String uuid) {
        mediaViewController.setView_uuid(uuid);
    }

    public String getUuid() {
        return mediaViewController.getView_uuid();
    }

    public void setVisible(boolean visible) {
        mediaViewController.setViewVisible(visible);
    }

    public boolean isLayoutVisible() {
        return mediaViewController.isViewVisible();
    }

    public void setVisibility(int visibility) {
        if (mediaViewController != null){
            mediaViewController.getMediaView().setVisibility(visibility);
        }
    }

    public void setActiveSpeaker(boolean active) {
        mediaViewController.setActiveSpeaker(active);
    }

    public void setDisplayName(String displayName) {
        mediaViewController.setDisplayName(displayName);
    }

    public void hideNameView(){
        mediaViewController.hideDisplayNameTextView();
    }

    public void setAudioMute(boolean audioMuted){
        mediaViewController.setAudioMuted(audioMuted);
    }

    public void setPinned(boolean pinned){
        mediaViewController.setPinned(pinned);
    }

    public void showContentWaterView(boolean show, String name) {
        mediaViewController.setContentWatermarkVisible(show, name);
    }

    public void setLoadingStatus(boolean showPic) {
        mediaViewController.setLoading(showPic);
    }


    public void invalidate() {
        mediaViewController.getMediaView().invalidate();
    }


    public void setViewRender(boolean enabled){
        mediaViewController.enableRender(enabled);
    }

    public boolean isEnabledViewRender(){
        return mediaViewController != null && mediaViewController.isRenderEnabled();
    }

    public void reqViewRender() {
        if (mediaViewController.isViewVisible()) {
            mediaViewController.reqRender();
        }
    }
}
