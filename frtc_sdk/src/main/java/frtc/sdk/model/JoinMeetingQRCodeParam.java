package frtc.sdk.model;

public class JoinMeetingQRCodeParam extends CommonParam {
    private String qrcode;
    private String displayName;
    public JoinMeetingQRCodeParam() {

    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
