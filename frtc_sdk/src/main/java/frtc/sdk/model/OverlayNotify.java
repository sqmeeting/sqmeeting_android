package frtc.sdk.model;

public class OverlayNotify {
    private boolean enabled;
    private String content;
    private int repeat;
    private String speed;
    private String type;
    private int verticaPosition;

    public OverlayNotify() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVerticaPosition() {
        return verticaPosition;
    }

    public void setVerticaPosition(int verticaPosition) {
        this.verticaPosition = verticaPosition;
    }
}
