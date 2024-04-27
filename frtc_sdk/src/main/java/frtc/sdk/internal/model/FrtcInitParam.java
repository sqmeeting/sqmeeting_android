package frtc.sdk.internal.model;

public class FrtcInitParam {
    private String my_uuid;
    private int cpu_level;
    private String log_path;

    public String getMy_uuid() {
        return my_uuid;
    }

    public void setMy_uuid(String my_uuid) {
        this.my_uuid = my_uuid;
    }

    public int getCpu_level() {
        return cpu_level;
    }

    public void setCpu_level(int cpu_level) {
        this.cpu_level = cpu_level;
    }

    public String getLog_path() {
        return log_path;
    }

    public void setLog_path(String log_path) {
        this.log_path = log_path;
    }
}
