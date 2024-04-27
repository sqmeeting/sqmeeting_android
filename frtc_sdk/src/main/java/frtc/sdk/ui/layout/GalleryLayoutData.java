package frtc.sdk.ui.layout;

public class GalleryLayoutData {

    private int index;
    private float left;
    private float top;
    private float size;

    public GalleryLayoutData(int index, float left, float top, float size) {
        this.index = index;
        this.left = left;
        this.top = top;
        this.size = size;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}
