package frtc.sdk.ui.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.ui.media.IMediaViewController;
import frtc.sdk.ui.model.OperationType;
import frtc.sdk.ui.model.ViewConstant;
import frtc.sdk.util.StringUtils;
import frtc.sdk.util.VideoScaleEndAnimator;

public class MediaFrameLayout extends FrameLayout implements IMediaViewController {
    private final String TAG = MediaFrameLayout.class.getSimpleName();

    private Context ctx;
    protected String display_name;
    private volatile String view_uuid;
    protected volatile boolean active_speaker;
    private boolean enabled_render = false;
    private volatile boolean view_visible = false;
    private MediaTextureView media_view;

    private volatile boolean running = false;

    private TextView tv_display_name;
    private TextView tv_watermark;
    private TextView tx_loading;

    private float display_name_scale = 1;

    private OperationType opType = OperationType.OP_NONE;

    private boolean enabled_scale_touch;
    private float center_start_x;
    private float center_start_y;
    private float center_last_x;
    private float center_last_y;
    private float last_span;
    private float cur_span;
    private float scale = 1.0F;
    private final Matrix matrix = new Matrix();
    private Matrix scale_matrix;
    private final float[] matrix_data = new float[9];


    private float last_x;
    private float last_y;
    private VideoScaleEndAnimator videoAnimator;
    private boolean loading = false;
    private boolean audio_muted = false;
    private boolean pinned = false;

    public MediaFrameLayout(Context context) {
        super(context);
        ctx = context;
        media_view = new MediaTextureView(context);
        this.addView(media_view, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setLoadingTextView();
        setDisplayNameTextView();
        setContentWatermarkTextView();
    }

    public void doScale(ScaleGestureDetector detector) {
        opType = OperationType.OP_ZOOM;
        if (enabled_scale_touch) {
            cur_span = detector.getCurrentSpan();
            if (handleScale(detector)) {
                center_last_x = detector.getFocusX();
                center_last_y = detector.getFocusY();
                last_span = cur_span;
            }
        }
    }
    public float getScale(){
        return scale;
    }

    public void doScaleStart(ScaleGestureDetector detector) {
        opType = OperationType.OP_ZOOM;
        enabled_scale_touch = true;
        if (scale_matrix == null) {
            scale_matrix = new Matrix(media_view.getMatrix());
            updateScaleMatrix(scale_matrix);
        }
        center_start_x = detector.getFocusX();
        center_start_y = detector.getFocusY();
        center_last_x = center_start_x;
        center_last_y = center_start_y;
        last_span = detector.getCurrentSpan();
    }

    public void doScaleEnd() {
        if (enabled_scale_touch) {
            enabled_scale_touch = false;
            doScaleEndAnimation();
        }
    }

    private void doScaleEndAnimation() {
        Matrix scaleEndAnimMatrix = new Matrix();
        RectF videoRectF = new RectF(0, 0, media_view.getWidth(), media_view.getHeight());
        if (scale > 0 && scale <= 1.0f) {
            scaleEndAnimMatrix.postScale(scale, scale, videoRectF.right / 2, videoRectF.bottom / 2);
            startAnimationEnd(scale_matrix, scaleEndAnimMatrix);
        } else if (scale > 1.0F) {
            RectF rectF = new RectF(0, 0, media_view.getWidth(), media_view.getHeight());
            scale_matrix.mapRect(rectF);
            float transAnimX = 0f;
            float transAnimY = 0f;
            scaleEndAnimMatrix.set(scale_matrix);
            if (rectF.left > videoRectF.left
                    || rectF.right < videoRectF.right
                    || rectF.top > videoRectF.top
                    || rectF.bottom < videoRectF.bottom) {
                if (rectF.left > videoRectF.left) {
                    transAnimX = videoRectF.left - rectF.left;
                } else if (rectF.right < videoRectF.right) {
                    transAnimX = videoRectF.right - rectF.right;
                }
                if (rectF.top > videoRectF.top) {
                    transAnimY = videoRectF.top - rectF.top;
                } else if (rectF.bottom < videoRectF.bottom) {
                    transAnimY = videoRectF.bottom - rectF.bottom;
                }
                scaleEndAnimMatrix.postTranslate(transAnimX, transAnimY);
                startAnimationEnd(scale_matrix, scaleEndAnimMatrix);
            }
        }
    }

    private void startAnimationEnd(Matrix startMatrix, Matrix endMatrix) {
        if (videoAnimator != null) {
            videoAnimator.cancel();
            videoAnimator = null;
        }
        videoAnimator = new VideoScaleEndAnimator(startMatrix, endMatrix) {

            @Override
            protected void updateMatrixToView(Matrix transMatrix) {
                if (media_view != null) {
                    media_view.setTransform(transMatrix);
                }
                updateScaleMatrix(transMatrix);
            }
        };
        videoAnimator.start();
        scale_matrix = endMatrix;
    }

    public void handleDoubleTap(MotionEvent e) {
        float scale = this.scale == 1.0f ? ViewConstant.MAX_SCALE : ViewConstant.MIN_SCALE;
        if (scale_matrix == null) {
            scale_matrix = new Matrix(media_view.getMatrix());
        }
        float diffScale = scale / this.scale;
        doPostMatrixScale(scale_matrix, diffScale, e.getX(), e.getY());
        updateScaleMatrix(scale_matrix);
        if (media_view != null) {
            Matrix matrix = new Matrix(media_view.getMatrix());
            matrix.set(scale_matrix);
            media_view.setTransform(matrix);
        }
        if(scale == 1.0f){
            enabled_scale_touch = false;
            if(scale_matrix != null) {
                scale_matrix.reset();
                updateScaleMatrix(scale_matrix);
            }
            matrix.reset();
            media_view.setTransform(matrix);
            center_last_x = 0.0F;
            center_last_y = 0.0F;
        }
    }

    public void handleDown(MotionEvent e) {
        opType = OperationType.OP_DRAG;
        last_x = e.getX();
        last_y = e.getY();
    }

    public void handleScroll(MotionEvent e) {
        if (opType == OperationType.OP_DRAG && scale >1) {
            if (scale_matrix == null) {
                scale_matrix = new Matrix(media_view.getMatrix());
                updateScaleMatrix(scale_matrix);
            }
            scale_matrix.postTranslate(e.getX() - last_x, e.getY() - last_y);
            last_x = e.getX();
            last_y = e.getY();
            media_view.setTransform(scale_matrix);
            doScaleEndAnimation();
        }
    }

    private void updateScaleMatrix(Matrix matrix) {
        matrix.getValues(matrix_data);
        scale = matrix_data[Matrix.MSCALE_X];
        media_view.invalidate();
    }

    private boolean handleScale(ScaleGestureDetector detector) {
        if (scale_matrix != null) {
            doPostMatrixScale(scale_matrix, cur_span / last_span, center_start_x, center_start_y);
            scale_matrix.postTranslate(detector.getFocusX() - center_last_x,detector.getFocusY() - center_last_y);
            updateScaleMatrix(scale_matrix);
            if (media_view != null) {
                Matrix matrix = new Matrix(media_view.getMatrix());
                matrix.set(scale_matrix);
                media_view.setTransform(matrix);
            }
            return true;
        }
        return false;
    }

    private void doPostMatrixScale(Matrix matrix, float scale, float x, float y) {
        matrix.getValues(matrix_data);
        float curScale = matrix_data[Matrix.MSCALE_X];
        if (scale < 1 && Math.abs(curScale - ViewConstant.MIN_SCALE) < 0.001F) {
            scale = 1;
        } else if (scale > 1 && Math.abs(curScale - ViewConstant.MAX_SCALE) < 0.001F) {
            scale = 1;
        } else {
            curScale *= scale;
            if (scale < 1 && curScale < ViewConstant.MIN_SCALE) {
                curScale = ViewConstant.MIN_SCALE;
                scale = curScale / matrix_data[Matrix.MSCALE_X];
            } else if (scale > 1 && curScale > ViewConstant.MAX_SCALE) {
                curScale = ViewConstant.MAX_SCALE;
                scale = curScale / matrix_data[Matrix.MSCALE_X];
            }
            matrix.postScale(scale, scale, x, y);
        }
    }

    private void setLoadingTextView() {
        tx_loading = new TextView(ctx);
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tx_loading.setBackgroundResource(R.drawable.bg_loading);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
            tx_loading.setBackgroundResource(R.drawable.bg_loading_p);
        }
        LayoutParams labelParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        tx_loading.setVisibility(INVISIBLE);
        tx_loading.setSingleLine();
        tx_loading.setTextColor(Color.WHITE);
        tx_loading.setTextSize(ViewConstant.LOADING_TEXT_TEXT_SIZE);
        tx_loading.setEllipsize(TextUtils.TruncateAt.END);
        tx_loading.setGravity(Gravity.CENTER);
        tx_loading.setLayoutParams(labelParams);
        this.addView(tx_loading);
    }

    private void setDisplayNameTextView() {
        tv_display_name = new TextView(ctx);
        tv_display_name.setBackgroundResource(R.drawable.bg_name);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        tv_display_name.setLayoutParams(layoutParams);
        tv_display_name.setGravity(Gravity.CENTER_VERTICAL);
        tv_display_name.setPadding(ViewConstant.padding_left, 0, 0, ViewConstant.padding_bottom);
        tv_display_name.setSingleLine();
        tv_display_name.setEllipsize(TextUtils.TruncateAt.END);
        tv_display_name.setTextSize(ViewConstant.DISPLAY_NAME_TEXT_SIZE);
        tv_display_name.setTextColor(Color.WHITE);
        this.addView(tv_display_name);
    }

    private void setContentWatermarkTextView() {
        tv_watermark = new TextView(ctx);
        tv_watermark.setBackgroundResource(R.color.transparent);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        tv_watermark.setPadding(ViewConstant.padding_left, 0, 0, ViewConstant.padding_bottom);
        tv_watermark.setLayoutParams(layoutParams);
        tv_watermark.setGravity(Gravity.CENTER_VERTICAL);
        tv_watermark.setSingleLine();
        tv_watermark.setText("");
        tv_watermark.setTextSize(ViewConstant.WATERMARK_TEXT_SIZE);
        tv_watermark.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tv_watermark.setTextColor(ContextCompat.getColor(getContext(), R.color.content_water_color));
        tv_watermark.setVisibility(GONE);
        tv_watermark.setEllipsize(TextUtils.TruncateAt.END);
        tv_watermark.setRotation(ViewConstant.ROTATION);
        this.addView(tv_watermark);
    }

    public void setLoadingTextView(int orientation) {
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tx_loading.setBackgroundResource(R.drawable.bg_loading);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
            tx_loading.setBackgroundResource(R.drawable.bg_loading_p);
        }
    }

    @Override
    public void reqRender() {
        if (media_view != null && view_visible){
            media_view.notifyRender();
        }
    }

    @Override
    public View getMediaView() {
        return this;
    }

    @Override
    public String getMsid() {
        return media_view == null ? null : media_view.getMsid();
    }

    @Override
    public void setMsid(String id) {
        if (media_view != null){
            media_view.setMsid(id);
        }
    }

    @Override
    public String getDisplayName() {
        return this.display_name;
    }

    @Override
    public void destroy(ViewGroup parentViewGroup) {
        if(media_view != null){
            media_view.setVisibility(View.GONE);
            this.removeView(media_view);
        }
        this.setVisibility(View.GONE);
        parentViewGroup.removeView(this);
    }

    @Override
    public void setDisplayName(String name) {
        try {
            this.display_name = name;
            if (loading) {
                tv_display_name.setText("");
            } else {
                tv_display_name.setText(name);
            }
            tx_loading.setText(name);
        } catch (Exception e) {
            Log.e(TAG, "setDisplayName Exception :" + e.getMessage());
        }
    }

    public void resizeDisplayNameTextView(int oldWidth, int newWidth) {
        if (oldWidth > newWidth) {
            display_name_scale = 2;
        } else if (oldWidth < newWidth) {
            display_name_scale = (float) 0.5;
        } else {
            display_name_scale = 1;
        }
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        if (tv_display_name != null) {
            tv_display_name.setTextSize(14 * display_name_scale);
            tv_display_name.setLayoutParams(layoutParams);
            tv_display_name.setPadding((int) (ViewConstant.padding_top * display_name_scale), 0, 0, (int) (ViewConstant.padding_bottom * display_name_scale));
            resizeDrawable(display_name_scale);
            setDisplayNameVisible(true);
        }
        LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (tx_loading != null) {
            tx_loading.setTextSize(28 * display_name_scale);
            tx_loading.setLayoutParams(lParams);
        }
    }

    public void setDisplayNameVisible(boolean visible) {
        try {
            tv_display_name.setVisibility(visible ? VISIBLE : INVISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "setDisplayNameVisible Exception:" + e.getMessage());
        }
    }

    @Override
    public void setAudioMuted(boolean muted) {
        this.audio_muted = muted;
        resizeDrawable(display_name_scale);
    }

    @Override
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
        resizeDrawable(display_name_scale);
    }

    @Override
    public void hideDisplayNameTextView() {
        setDisplayNameVisible(false);
    }

    @Override
    public void setActiveSpeaker(boolean active) {
        this.active_speaker = active;
    }

    @Override
    public boolean isViewVisible() {
        return view_visible;
    }

    @Override
    public void setViewVisible(boolean visible) {
        this.view_visible = visible;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return false;
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        invalidateOutline();
        Rect rect = new Rect();
        if (canvas.getClipBounds(rect) && canvas.clipRect(rect)) {
            for (int i = 0; i < getCheckActiveBorder(); i++) {
                canvas.drawRect(rect.left + i, rect.top + i, rect.right - i, rect.bottom - i, getPaint());
            }
        }
        if (this.active_speaker) {
            if (!running) {
                running = true;
                postInvalidateDelayed(ViewConstant.interval);
                postDelayed(() -> running = false, ViewConstant.delayDraw);
            }
        }
    }

    @Override
    public void setLoading(boolean loading) {
        try {
            this.loading = loading;
            if (this.loading) {
                tx_loading.setVisibility(VISIBLE);
                tv_display_name.setText("");
            } else {
                tx_loading.setVisibility(INVISIBLE);
                tv_display_name.setText(display_name);
            }
            invalidate();
        } catch (Exception e) {
            Log.e(TAG, "setLoading Exception:" + e);
        }
    }

    @Override
    public void setView_uuid(String uuid) {
        this.view_uuid = uuid;
    }

    @Override
    public String getView_uuid() {
        return this.view_uuid;
    }

    @Override
    public void enableRender(boolean enabled) {
        this.enabled_render = enabled;
    }

    @Override
    public boolean isRenderEnabled() {
        return enabled_render;
    }

    @Override
    public void setContentWatermarkVisible(boolean visible, String text) {
        try {
            if (visible) {
                if(StringUtils.isBlank(text)){
                    return;
                }
                tv_watermark.setVisibility(VISIBLE);
                StringBuilder sb = new StringBuilder(text);
                if (sb.length() < 21 && sb.length() != 0) {
                    for (int i = 0; i < 20 / text.length(); i++) {
                        sb.append(" ");
                        sb.append(text);
                    }
                }
                tv_watermark.setText(sb.toString());
            } else {
                tv_watermark.setVisibility(GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "setContentWatermarkVisible Exception:" + e.getMessage());
        }
    }

    public void resetContentScale() {
        try {
            media_view.setScaleX(ViewConstant.SCALE_X);
            media_view.setScaleY(ViewConstant.SCALE_Y);
            enabled_scale_touch = false;
            if(scale_matrix != null) {
                scale_matrix.reset();
                updateScaleMatrix(scale_matrix);
            }
            matrix.reset();
            media_view.setTransform(matrix);
            center_last_x = 0.0F;
            center_last_y = 0.0F;
        } catch (Exception e) {
            Log.e(TAG, "resetContentScale Exception:" + e.getMessage());
        }
    }

    private void resizeDrawable(float displayNameScale) {
        try {
            Drawable drawable;
            if (pinned) {
                if (audio_muted) {
                    drawable = this.ctx.getResources().getDrawable(R.drawable.pinned_muted);
                }else{
                    drawable = this.ctx.getResources().getDrawable(R.drawable.pinned_unmuted);
                }
            } else {
                if (audio_muted) {
                    drawable = this.ctx.getResources().getDrawable(R.drawable.status_mute);
                } else {
                    drawable = this.ctx.getResources().getDrawable(R.drawable.status_unmute);
                }
            }
            if (drawable != null) {
                int w = (int) (drawable.getIntrinsicWidth() * displayNameScale);
                int h = (int) (drawable.getIntrinsicHeight() * displayNameScale);
                drawable.setBounds(0, 0, w, h);
                tv_display_name.setCompoundDrawables(null, null, drawable, null);
            } else {
                tv_display_name.setCompoundDrawables(null, null, null, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "resizeDrawable Exception:" + e.getMessage());
        }
    }

    private int getCheckActiveBorder() {
        int width = this.ctx.getResources().getDimensionPixelSize(R.dimen.media_border_width);
        if (this.active_speaker) {
            return width * 2;
        }
        return width;
    }

    private int getCheckActiveColor() {
        if (this.active_speaker) {
            return this.ctx.getResources().getColor(R.color.video_border_active_speaker);
        }
        return this.ctx.getResources().getColor(R.color.video_border_participant);
    }

    private Paint getPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getCheckActiveColor());
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

}
