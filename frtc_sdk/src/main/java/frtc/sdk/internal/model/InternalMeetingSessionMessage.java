package frtc.sdk.internal.model;

public class InternalMeetingSessionMessage {

    private String watermark_msg;
    private String streaming_url;

    private String streaming_pwd;
    private String streaming_status;

    private String recording_status;

    public String getWatermark_msg() {
        return watermark_msg;
    }

    public void setWatermark_msg(String watermark_msg) {
        this.watermark_msg = watermark_msg;
    }

    public String getStreaming_url() {
        return streaming_url;
    }

    public void setStreaming_url(String streaming_url) {
        this.streaming_url = streaming_url;
    }

    public String getStreaming_pwd() {
        return streaming_pwd;
    }

    public void setStreaming_pwd(String streaming_pwd) {
        this.streaming_pwd = streaming_pwd;
    }

    public String getStreaming_status() {
        return streaming_status;
    }

    public void setStreaming_status(String streaming_status) {
        this.streaming_status = streaming_status;
    }

    public String getRecording_status() {
        return recording_status;
    }

    public void setRecording_status(String recording_status) {
        this.recording_status = recording_status;
    }
}
