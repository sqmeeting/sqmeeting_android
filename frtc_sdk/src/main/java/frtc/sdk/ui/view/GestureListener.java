package frtc.sdk.ui.view;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import frtc.sdk.log.Log;
import frtc.sdk.ui.model.ViewConstant;
import frtc.sdk.ui.media.MediaModule;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private final String TAG = GestureListener.class.getSimpleName();

    private MeetingLayout meetingLayout;
    private SwipeTouchListener swipeTouchListener;

    public GestureListener(MeetingLayout meetingLayout, SwipeTouchListener swipeTouchListener) {
        this.meetingLayout = meetingLayout;
        this.swipeTouchListener = swipeTouchListener;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (meetingLayout.getLayout_manager() != null && meetingLayout.getLayout_manager().isCurrentContentPage()
                && meetingLayout.getContentList() != null && !meetingLayout.getContentList().isEmpty()) {
            for (MediaModule content : meetingLayout.getContentList()){
                if(content.getMediaView() instanceof MediaFrameLayout){
                    ((MediaFrameLayout) content.getMediaView()).handleDoubleTap(e);
                    if(((MediaFrameLayout) content.getMediaView()).getScale() == ViewConstant.scale_1
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
    public boolean onDown(MotionEvent e) {
        if (meetingLayout.getLayout_manager() != null && meetingLayout.getLayout_manager().isCurrentContentPage()
                && meetingLayout.getContentList() != null && !meetingLayout.getContentList().isEmpty()) {
            for (MediaModule content : meetingLayout.getContentList()){
                if(content.getMediaView() instanceof MediaFrameLayout){
                    ((MediaFrameLayout) content.getMediaView()).handleDown(e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        if (meetingLayout.getContentList() != null && !meetingLayout.getContentList().isEmpty()) {
            for (MediaModule content : meetingLayout.getContentList()) {
                if (content.getMediaView() instanceof MediaFrameLayout) {
                    float scale = ((MediaFrameLayout) content.getMediaView()).getScale();
                    if (scale != ViewConstant.scale_1) {
                        return false;
                    }
                }
            }
        }
        boolean flag = false;
        try {
            float dY = me2.getY() - me1.getY();
            float dX = me2.getX() - me1.getX();
            if (Math.abs(dX) > Math.abs(dY)) {
                if (Math.abs(dX) > ViewConstant.thresholdFling && Math.abs(velocityX) > ViewConstant.thresholdVelocity) {
                    if (dX > 0) {
                        this.swipeTouchListener.onSwipeRight();
                    } else {
                        this.swipeTouchListener.onSwipeLeft();
                    }
                    flag = true;
                }
            } else if (Math.abs(dY) > ViewConstant.thresholdFling && Math.abs(velocityY) > ViewConstant.thresholdVelocity) {
                if (dY > 0) {
                    this.swipeTouchListener.onSwipeBottom();
                } else {
                    this.swipeTouchListener.onSwipeTop();
                }
                flag = true;
            }
        } catch (Exception e) {
            Log.e("GestureListener","onFling: ", e);
        }
        return flag;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (meetingLayout.getLayout_manager() != null && meetingLayout.getLayout_manager().isCurrentContentPage()
                && meetingLayout.getContentList() != null && !meetingLayout.getContentList().isEmpty()) {
            for (MediaModule content : meetingLayout.getContentList()) {
                if (content.getMediaView() instanceof MediaFrameLayout) {
                    ((MediaFrameLayout) content.getMediaView()).handleScroll(e2);
                }
            }
        }
        return false;

    }
}