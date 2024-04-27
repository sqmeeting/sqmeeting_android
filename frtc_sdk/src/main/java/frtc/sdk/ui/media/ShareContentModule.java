package frtc.sdk.ui.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import frtc.sdk.R;
import frtc.sdk.util.AnimationBuilder;

public class ShareContentModule extends MediaModule {
    private final String TAG = ShareContentModule.class.getSimpleName();
    protected AnimationBuilder animationBuilder = null;

    private View localContentView = null;
    private String msid = "";
    private String uuid = "";

    private IShareContentControlListener listener = null;

    public ShareContentModule(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        localContentView = inflater.inflate(R.layout.local_content_page,null);
        animationBuilder = new AnimationBuilder(localContentView);
        ImageView stopContentBtn = localContentView.findViewById(R.id.stop_content_btn);
        stopContentBtn.setOnClickListener(view -> {
            onViewClick();
        });
        localContentView.setOnTouchListener(this);
    }

    public interface IShareContentControlListener{
        void onStopContent();
    }

    public void setShareContentListener(IShareContentControlListener listener){
        this.listener = listener;
    }

    @Override
    public void setMsid(String msid){
        this.msid = msid;
    }

    @Override
    public String getMsid(){
        return msid;
    }

    private View getView(){
        return localContentView;
    }

    @Override
    public View getMediaView(){
        return localContentView;
    }

    @Override
    public void destroy(ViewGroup parent) {
        parent.removeView(localContentView);
        if (animationBuilder != null) {
            animationBuilder.resetAll();
            animationBuilder = null;
        }
        listener = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(MediaModule cell){

    }

    private void onViewClick(){
        if(listener != null){
            listener.onStopContent();
        }
    }

    public void invalidate(){
        if(localContentView != null){
            localContentView.invalidate();
        }
    }

    @Override
    public void setVisible(boolean visible){

    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void doCreateAnimation(int w, int h, int mLeft, int mTop){
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

    @Override
    public void doUpdateAnimation(int w, int h, int mLeft, int mTop){
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
            getView().setTranslationX(0.0f);
            getView().setTranslationY(0.0f);
            animationBuilder.setViewWidth(w).setViewHeight(h).
                    setLeftMargin(mLeft).setTopMargin(mTop).setTranslationX(this.getView().getTranslationX()).
                    setTranslationY(this.getView().getTranslationY()).build().
                    startPositionAnimation().setDuration(0).start().resetAnimation().
                    buildScale().setDuration(100).start();
            invalidate();
        }
    }

    @Override
    public void doHidden(){
        getView().setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
        getView().setTranslationX(0.0f);
        getView().setTranslationY(0.0f);
        if (animationBuilder != null)
            animationBuilder.resetData();
    }

    @Override
    public boolean isRendered(){
        return animationBuilder != null && animationBuilder.isAnimationRendered();
    }

    @Override
    public boolean isLayoutVisible() {
        return getView().getVisibility() == View.VISIBLE;
    }

    @Override
    public void doScale(float scale){
    }

    @Override
    public boolean isRunning(){
        return animationBuilder != null && animationBuilder.isAnimationRunning();
    }

    @Override
    public void setLocalView(int width, int height, int marginLeft, int marginTop){
        if (animationBuilder != null) {
            if(animationBuilder.isAnimationRunning()){
                animationBuilder.cancel();
            }
            animationBuilder.clearData();
            animationBuilder.setViewWidth(width).setViewHeight(height).
                    setLeftMargin(marginLeft).setTopMargin(marginTop).setTranslationX(this.getView().getTranslationX()).
                    setTranslationY(this.getView().getTranslationY()).buildTransition().
                    buildScale().setDuration(10).start();
            invalidate();
        }
    }


    @Override
    public void setViewRender(boolean enabled){
    }

    @Override
    public boolean isEnabledViewRender(){
        return false;
    }

    @Override
    public void reqViewRender() {
    }

}
