package frtc.sdk.ui.view;

import android.view.ScaleGestureDetector;
import android.view.View;

import frtc.sdk.ui.media.MediaModule;

public class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    private MeetingLayout  meetingLayout;

    public ScaleGestureListener(MeetingLayout meetingLayout) {
        this.meetingLayout = meetingLayout;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (meetingLayout.getLayout_manager() != null && meetingLayout.getLayout_manager().isCurrentContentPage()
                && meetingLayout.getContentList() != null && !meetingLayout.getContentList().isEmpty()) {
            for (MediaModule content : meetingLayout.getContentList()){
                if(content.getMediaView() instanceof MediaFrameLayout){
                    ((MediaFrameLayout) content.getMediaView()).doScale(detector);
                    if(((MediaFrameLayout) content.getMediaView()).getScale() == 1.0
                            && !meetingLayout.isControlBar_visible()) {
                        meetingLayout.getCallSlideView().setVisibility(View.VISIBLE);
                    }else{
                        meetingLayout.getCallSlideView().setVisibility(View.GONE);
                    }
                }
            }
        }
        return false;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (meetingLayout.getLayout_manager() != null && meetingLayout.getLayout_manager().isCurrentContentPage()
                && meetingLayout.getContentList() != null && !meetingLayout.getContentList().isEmpty()) {
            for (MediaModule content : meetingLayout.getContentList()){
                if(content.getMediaView() instanceof MediaFrameLayout){
                    ((MediaFrameLayout) content.getMediaView()).doScaleStart(detector);
                }
            }
        }
        return true;
    }
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (meetingLayout.getLayout_manager() != null && meetingLayout.getLayout_manager().isCurrentContentPage()
                && meetingLayout.getContentList() != null && !meetingLayout.getContentList().isEmpty()) {
            for (MediaModule content : meetingLayout.getContentList()){
                if(content.getMediaView() instanceof MediaFrameLayout){
                    ((MediaFrameLayout) content.getMediaView()).doScaleEnd();
                }
            }
        }
    }
}