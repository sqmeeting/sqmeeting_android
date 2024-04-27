package frtc.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import frtc.sdk.R;
import frtc.sdk.ui.device.CameraCallback;

public class MediaSurfaceView extends SurfaceView {
    private static final String TAG = MediaSurfaceView.class.getSimpleName();

    private Context ctx;
    private boolean active_speaker;
    private boolean view_visible;
    private String display_name;
    private boolean muted = false;
    private Bitmap mute_bitmap = null;

    private final float DEFAULT_ELEVATION = 10.0f;
    private final int DEFAULT_ALPHA = 255;

    public MediaSurfaceView(Context ctx) {
        super(ctx);
        this.ctx = ctx;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private int getDefaultBorder() {
        return this.ctx.getResources().getDimensionPixelSize(R.dimen.media_border_width);
    }

    private int getCheckActiveBorder() {
        int width = this.ctx.getResources().getDimensionPixelSize(R.dimen.media_border_width);
        if (active_speaker) {
            return width * 2;
        }
        return width;
    }
    private int getCheckActiveColor() {
        if (active_speaker) {
            return this.ctx.getResources().getColor(R.color.video_border_active_speaker);
        }
        return this.ctx.getResources().getColor(R.color.video_border_participant);
    }

    private int getBlack() {
        return Color.parseColor("#000000");
    }

    public void setVideoMuted(boolean muted, int orientation) {
        this.muted = muted;
        if (this.muted) {
            if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
                mute_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.call_camera_off_bg);
            }else if(Configuration.ORIENTATION_PORTRAIT == orientation) {
                mute_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.call_camera_off_bg_p);
            }
        } else {
            mute_bitmap = null;
        }
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        invalidateOutline();
        setElevation(DEFAULT_ELEVATION);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Style.STROKE);
        Rect rect = new Rect();
        if (canvas.getClipBounds(rect) && canvas.clipRect(rect)) {
            paint.setColor(getCheckActiveColor());
            for (int i = 0; i < getCheckActiveBorder(); i++) {
                canvas.drawRect(rect.left + i, rect.top + i, rect.right - i, rect.bottom - i, paint);
            }
        }
        if (mute_bitmap != null) {
            if (muted) {
                canvas.drawBitmap(mute_bitmap, null, rect, paint);
                paint.setColor(getBlack());
                paint.setAlpha(DEFAULT_ALPHA);
                for (int i = 0; i < getDefaultBorder() * 4; i++) {
                    canvas.drawRect(rect.left + i, rect.top + i, rect.right - i, rect.bottom - i, paint);
                }
            }
        }
    }

    public View getView(){
        return this;
    }

    public void setCameraCallback(CameraCallback callback) {
        this.getHolder().addCallback(callback);
    }

    public String getName() {
        return display_name;
    }

    public void setName(String displayName) {
        this.display_name = displayName;
        invalidate();
    }

    public boolean isActiveSpeakerView() {
        return active_speaker;
    }

    public void setViewActiveSpeaker(boolean active) {
        this.active_speaker = active;
    }

    public boolean isViewVisible() {
        return this.view_visible;
    }

    public void setViewVisible(boolean visible) {
        this.view_visible = visible;
    }

}

