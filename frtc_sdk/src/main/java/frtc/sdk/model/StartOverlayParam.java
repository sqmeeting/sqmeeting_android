package frtc.sdk.model;

public class StartOverlayParam extends CommonParam {
    private String meetingNumber;
    private String content;
    private int repeat;
    private int position;
    private boolean enable_scroll;

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isScroll() {
        return enable_scroll;
    }

    public void setScroll(boolean scroll) {
        this.enable_scroll = scroll;
    }
}