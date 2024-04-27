package frtc.sdk.ui.layout;

public interface ILayoutHandler {
    void setLocalView(int w, int h, int mLeft, int mTop);
    void doLayoutAnimation(int w, int h, int mLeft, int mTop);
    void doScale(float scale);
    void doHidden();
    boolean isRendered();
    boolean isLayoutVisible();
    boolean isRunning();
    void doCreateAnimation(int w, int h, int mLeft, int mTop);
    void doUpdateAnimation(int w, int h, int mLeft, int mTop);

}
