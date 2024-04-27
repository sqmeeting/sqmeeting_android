package frtc.sdk.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

public class AnimationBuilder {

    private AnimatorSet aSet;
    private int animatorDuration = -1;

    private Integer height = 0;
    private Integer width = 0;

    private RelativeLayout.LayoutParams layoutParams;
    private View view;

    private Integer viewHeight = 0;
    private Integer viewWidth = 0;
    private Float viewTranslationX = 0f;
    private Float viewTranslationY = 0f;
    private Integer leftMargin = 0;
    private Integer topMargin = 0;
    private Integer layoutMarginLeft = 0;
    private Integer layoutMarginTop = 0;

    private Integer iWidth = 0;
    private Integer iHeight = 0;

    public AnimationBuilder(View view) {
        this.view = view;
    }

    public AnimationBuilder setViewWidth(int pWidth) {
        viewWidth = pWidth;
        return this;
    }

    public AnimationBuilder setViewHeight(int pHeight) {
        viewHeight = pHeight;
        return this;
    }

    public AnimationBuilder setDuration(int animatorDuration) {
        this.animatorDuration = animatorDuration;
        return this;
    }

    public AnimationBuilder setLeftMargin(int pMarginLeft) {
        this.leftMargin = pMarginLeft;
        return this;
    }

    public AnimationBuilder setTopMargin(int pMarginTop) {
        this.topMargin = pMarginTop;
        return this;
    }

    public AnimationBuilder setTranslationX(float pTranslationX) {
        this.viewTranslationX = pTranslationX;
        return this;
    }

    public AnimationBuilder setTranslationY(float pTranslationY) {
        this.viewTranslationY = pTranslationY;
        return this;
    }

    public AnimationBuilder build() {
        if (view != null) {
            layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutMarginTop = layoutParams.topMargin;
            layoutMarginLeft = layoutParams.leftMargin;
            height = layoutParams.height;
            width = layoutParams.width;
        }
        return this;
    }

    private void setDirection(int len, Integer src, Integer dst, float[] direction) {
        if (src == null || len == 0) {
            direction[0] = 0;
            direction[1] = 1.0f;
        } else {
            direction[0] = (float) src / (float) len;
            direction[1] = (float) dst / (float) len;
        }
    }

    public AnimationBuilder buildScale() {
        if (view != null) {
            if (aSet == null) {
                aSet = new AnimatorSet();
            }
            float[] x = new float[2];
            float[] y = new float[2];
            setDirection(iWidth, width, viewWidth, x);
            setDirection(iHeight, height, viewHeight, y);
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, View.SCALE_X, x);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, View.SCALE_Y, y);
            aSet.playTogether(animatorX, animatorY);
        }
        return this;
    }

    public AnimationBuilder buildTransition() {
        if (view != null) {
            if (aSet == null) {
                aSet = new AnimatorSet();
            }
            int tempWidth = 0;
            if (width != null && width > 0) {
                tempWidth = (viewWidth - width) / 2;
            }
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(view,
                    View.TRANSLATION_X,
                    viewTranslationX,
                    viewTranslationX + leftMargin - layoutMarginLeft + tempWidth);
            int tempHeight = 0;
            if (height != null && height > 0) {
                tempHeight = (viewHeight - height) / 2;
            }
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y,
                    viewTranslationY,
                    viewTranslationY + topMargin - layoutMarginTop + tempHeight);
            aSet.playTogether(animatorX, animatorY);
        }
        return this;
    }

    public boolean isAnimationRunning() {
        if (aSet == null) {
            return false;
        }
        return aSet.isRunning();
    }

    public boolean isAnimationRendered() {
        if (view == null || view.getLayoutParams() == null) {
            return false;
        }
        return view.getLayoutParams().height != 0 &&
                view.getLayoutParams().width != 0;
    }


    public AnimationBuilder startPositionAnimation() {
        if (view != null && layoutParams != null) {
            layoutParams.leftMargin = leftMargin;
            layoutParams.topMargin = topMargin;
            layoutParams.width = viewWidth;
            layoutParams.height = viewHeight;
            view.setLayoutParams(layoutParams);
            iHeight = viewHeight;
            iWidth = viewWidth;
        }
        return this;
    }

    public AnimationBuilder start() {
        if (aSet != null) {
            int d;
            if (animatorDuration >= 0) {
                d = animatorDuration;
            } else {
                d = Constants.DURATION;
            }
            aSet.setDuration(d);
            aSet.setInterpolator(new AccelerateDecelerateInterpolator());
            aSet.start();
        }
        return this;
    }

    public void cancel() {
        if (aSet != null) {
            aSet.cancel();
        }
    }

    public AnimationBuilder addListener(Animator.AnimatorListener animatorListener) {
        if (aSet != null) {
            aSet.addListener(animatorListener);
        }
        return this;
    }

    public void clearData() {
        aSet = null;
        animatorDuration = -1;
        layoutParams = null;
        viewTranslationY = 0f;
        viewTranslationX = 0f;

        //
        height = viewHeight;
        width = viewWidth;
        viewHeight = 0;
        viewWidth = 0;

        //
        layoutMarginLeft = leftMargin;
        layoutMarginTop = topMargin;
        leftMargin = 0;
        topMargin = 0;
    }

    public AnimationBuilder resetAnimation() {
        if (aSet != null)
            aSet = null;
        return this;
    }

    public void resetData() {
        clearData();
        height = null;
        width = null;
        layoutMarginLeft = 0;
        layoutMarginTop = 0;
        iHeight = 0;
        iWidth = 0;
    }

    public void resetAll() {
        resetData();
        view = null;
    }

}
