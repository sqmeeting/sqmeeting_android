package frtc.sdk.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import frtc.sdk.R;


public class OverlayTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = OverlayTextView.class.getSimpleName();
    private boolean rolling = false;
    private int rollingCount = 0;
    private int timesNumber = 0;
    private int y = 0;
    private int x = 0;
    private int v;
    private String txtStr = "";
    private float tw = 0f;
    private int w = 0;
    private final int threshold = 20;
    private final int h = getResources().getDimensionPixelSize(R.dimen.text_view_height);
    private Paint paint = null;

    private RepeatEndListener listener;

    public void setText(String txtStr, int width, int speed, int repeat, boolean rolling) {

        this.txtStr = txtStr;
        this.v = speed;
        this.rollingCount = repeat;
        this.w = width;
        this.rolling = rolling;

        paint = this.getPaint();
        tw = paint.measureText(txtStr);

        if(rolling){
            x = this.w - threshold;
        }else{
            x = this.w;
        }

        Paint.FontMetrics metricsInt = paint.getFontMetrics();
        y = (int) ((h - (metricsInt.descent + metricsInt.ascent)) / 2);
        timesNumber = 0;

        invalidate();
    }


    public OverlayTextView(Context context) {
        super(context);
    }

    public OverlayTextView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public OverlayTextView(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context, attributeSet, defStyleAttr);
    }

    public void resetWidth(int width){

        int preWidth = this.w;
        this.w = width;
        if(rolling){
            x = x / preWidth * this.w;
        }else{
            x = this.w;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (rolling) {
            if (timesNumber == rollingCount) {
                if(listener != null){
                    listener.onRepeatEnd();
                }
                return;
            }
            paint.setColor(Color.WHITE);
            canvas.drawText(txtStr, x, y, paint);
            x = x - v;
            if (x + tw <= threshold) {
                x = w;
                timesNumber++;
            }
            invalidate();
        } else {
            paint.setColor(Color.WHITE);
            String displayStr = txtStr;
            if(w - tw < threshold){
                while (paint.measureText(displayStr) >= (w - paint.measureText("...") - threshold)) {
                    displayStr = displayStr.substring(0, displayStr.length() - 1);
                }
                displayStr += "...";
                float centerPos = (w - paint.measureText(displayStr)) / 2;
                canvas.drawText(displayStr, centerPos, y, paint);
            }else{
                float centerPos = (w - tw) / 2;
                canvas.drawText(txtStr, centerPos, y, paint);
            }
        }
        super.onDraw(canvas);
    }

    public void setRepeatEndListener(RepeatEndListener listener){
        this.listener = listener;
    }

    public interface RepeatEndListener{
        void onRepeatEnd();
    }

}
