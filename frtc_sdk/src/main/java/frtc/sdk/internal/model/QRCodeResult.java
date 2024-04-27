package frtc.sdk.internal.model;

public class QRCodeResult {
    private String meeting_number;
    private String server_address;

    private String meeting_passwd;
    private String username;
    private boolean full_screen;
    private String window_position;
    private int window_width;
    private int window_height;

    private String operation;
    private String meeting_url;
    private String meeting_id;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMeeting_url() {
        return meeting_url;
    }

    public void setMeeting_url(String meeting_url) {
        this.meeting_url = meeting_url;
    }

    public String getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(String meeting_id) {
        this.meeting_id = meeting_id;
    }

    public String getMeeting_number() {
        return meeting_number;
    }

    public void setMeeting_number(String meeting_number) {
        this.meeting_number = meeting_number;
    }

    public String getServer_address() {
        return server_address;
    }

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    public String getMeeting_passwd() {
        return meeting_passwd;
    }

    public void setMeeting_passwd(String meeting_passwd) {
        this.meeting_passwd = meeting_passwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isFull_screen() {
        return full_screen;
    }

    public void setFull_screen(boolean full_screen) {
        this.full_screen = full_screen;
    }

    public String getWindow_position() {
        return window_position;
    }

    public void setWindow_position(String window_position) {
        this.window_position = window_position;
    }

    public int getWindow_width() {
        return window_width;
    }

    public void setWindow_width(int window_width) {
        this.window_width = window_width;
    }

    public int getWindow_height() {
        return window_height;
    }

    public void setWindow_height(int window_height) {
        this.window_height = window_height;
    }
}
