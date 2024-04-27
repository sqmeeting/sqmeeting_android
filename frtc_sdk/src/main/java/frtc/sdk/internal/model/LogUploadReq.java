package frtc.sdk.internal.model;

public class LogUploadReq {

    private String meta_data;
    private String log_name;
    private int log_count;

    public String getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(String meta_data) {
        this.meta_data = meta_data;
    }

    public String getLog_name() {
        return log_name;
    }

    public void setLog_name(String log_name) {
        this.log_name = log_name;
    }

    public int getLog_count() {
        return log_count;
    }

    public void setLog_count(int log_count) {
        this.log_count = log_count;
    }
}
