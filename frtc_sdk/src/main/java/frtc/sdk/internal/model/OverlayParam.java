package frtc.sdk.internal.model;

public class OverlayParam {

    private boolean text_overlay_enable;
    private String text_overlay_type;
    private String text_overlay_msg;
    private int vertical_position;
    private int display_repetition;
    private String display_speed;

    public boolean isText_overlay_enable() {
        return text_overlay_enable;
    }

    public void setText_overlay_enable(boolean text_overlay_enable) {
        this.text_overlay_enable = text_overlay_enable;
    }

    public String getText_overlay_msg() {
        return text_overlay_msg;
    }

    public void setText_overlay_msg(String text_overlay_msg) {
        this.text_overlay_msg = text_overlay_msg;
    }

    public int getVertical_position() {
        return vertical_position;
    }

    public void setVertical_position(int vertical_position) {
        this.vertical_position = vertical_position;
    }

    public int getDisplay_repetition() {
        return display_repetition;
    }

    public void setDisplay_repetition(int display_repetition) {
        this.display_repetition = display_repetition;
    }

    public String getDisplay_speed() {
        return display_speed;
    }

    public void setDisplay_speed(String display_speed) {
        this.display_speed = display_speed;
    }

    public String getText_overlay_type() {
        return text_overlay_type;
    }

    public void setText_overlay_type(String text_overlay_type) {
        this.text_overlay_type = text_overlay_type;
    }
}
