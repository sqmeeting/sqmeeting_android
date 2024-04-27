package frtc.sdk.ui.model;

public enum MediaType {
    UNKNOWN("UNKNOWN"),
    LOCAL_PEOPLE("VPS"),
    LOCAL_AUDIO("APS"),
    REMOTE_AUDIO("APR"),
    REMOTE_PEOPLE("CID"),
    LOCAL_CONTENT("VCS"),
    REMOTE_CONTENT("VCR"),
    ;

    private String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
