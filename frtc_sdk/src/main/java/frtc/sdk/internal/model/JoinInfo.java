package frtc.sdk.internal.model;

public class JoinInfo {
    public String meeting_alias;
    public String server_addr;
    public final String display_name;
    public String user_token;
    public String meeting_pwd;
    public int meeting_uplink_bw;
    public int meeting_downlink_bw;
    public String user_id;

    public JoinInfo(String meeting_alias, String server_addr, String display_name, String user_token, String meeting_pwd, int meeting_uplink_bw, int meeting_downlink_bw, String user_id) {
        this.meeting_alias = meeting_alias;
        this.server_addr = server_addr;
        this.display_name = display_name;
        this.user_token = user_token;
        this.meeting_pwd = meeting_pwd;
        this.meeting_uplink_bw = meeting_uplink_bw;
        this.meeting_downlink_bw = meeting_downlink_bw;
        this.user_id = user_id;
    }

    public String getMeeting_alias() {
        return meeting_alias;
    }

    public void setMeeting_alias(String meeting_alias) {
        this.meeting_alias = meeting_alias;
    }

    public String getServer_addr() {
        return server_addr;
    }

    public void setServer_addr(String server_addr) {
        this.server_addr = server_addr;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getMeeting_pwd() {
        return meeting_pwd;
    }

    public void setMeeting_pwd(String meeting_pwd) {
        this.meeting_pwd = meeting_pwd;
    }

    public int getMeeting_uplink_bw() {
        return meeting_uplink_bw;
    }

    public void setMeeting_uplink_bw(int meeting_uplink_bw) {
        this.meeting_uplink_bw = meeting_uplink_bw;
    }

    public int getMeeting_downlink_bw() {
        return meeting_downlink_bw;
    }

    public void setMeeting_downlink_bw(int meeting_downlink_bw) {
        this.meeting_downlink_bw = meeting_downlink_bw;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
