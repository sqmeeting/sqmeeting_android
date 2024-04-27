package frtc.sdk.ui.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import frtc.sdk.log.Log;

public class SwipeTouchListener implements View.OnTouchListener {
    private final String TAG = SwipeTouchListener.class.getSimpleName();
    private final GestureDetector gestureDetector;
    private final ScaleGestureDetector detector;
    private MeetingLayout  meetingLayout;

    public SwipeTouchListener(Context ctx, MeetingLayout meetingLayout) {
        this.meetingLayout = meetingLayout;
        this.gestureDetector = new GestureDetector(ctx, new GestureListener(this.meetingLayout, this));
        this.detector = new ScaleGestureDetector(ctx, new ScaleGestureListener(this.meetingLayout));
    }

    public void onSwipeLeft() {
        Log.d(TAG, "onSwipeLeft");
    }
    public void onSwipeRight() {
        Log.d(TAG, "onSwipeRight");
    }

    public void onSwipeTop() {
        Log.d(TAG, "onSwipeTop");
    }

    public void onSwipeBottom() {
        Log.d(TAG, "onSwipeBottom");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        detector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

}