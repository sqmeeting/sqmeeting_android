package frtc.sdk.util;

import android.animation.ValueAnimator;
import android.graphics.Matrix;

public abstract class VideoScaleEndAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = "VideoScaleEndAnimator";

    public static final int SCALE_ANIMATOR_DURATION = 300;

    Matrix mTransMatrix = new Matrix();
    float[] mTransSpan = new float[2];
    float mLastValue;

    public VideoScaleEndAnimator(Matrix start, Matrix end) {
        this(start, end, SCALE_ANIMATOR_DURATION);
    }

    public VideoScaleEndAnimator(Matrix start, Matrix end, long duration) {
        super();
        setFloatValues(0, 1f);
        setDuration(duration);
        addUpdateListener(this);

        float[] startValues = new float[9];
        float[] endValues = new float[9];
        start.getValues(startValues);
        end.getValues(endValues);
        mTransSpan[0] = endValues[Matrix.MTRANS_X] - startValues[Matrix.MTRANS_X];
        mTransSpan[1] = endValues[Matrix.MTRANS_Y] - startValues[Matrix.MTRANS_Y];
        mTransMatrix.set(start);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float value = (Float) animation.getAnimatedValue();
        float transX = mTransSpan[0] * (value - mLastValue);
        float transY = mTransSpan[1] * (value - mLastValue);
        mTransMatrix.postTranslate(transX, transY);
        updateMatrixToView(mTransMatrix);
        mLastValue = value;
    }

    protected abstract void updateMatrixToView(Matrix transMatrix);
}
