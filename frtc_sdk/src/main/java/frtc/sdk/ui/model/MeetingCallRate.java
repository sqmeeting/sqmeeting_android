package frtc.sdk.ui.model;

public enum MeetingCallRate {

    RATE_128K("128K"),
    RATE_512K("512K"),
    RATE_1024K("1024K"),
    RATE_2048K("2048K"),
    RATE_2560K("2560K"),
    RATE_3072K("3072K"),
    RATE_4096K("4096K"),
    RATE_6144K("6144K"),
    RATE_8192K("8192K");

    private String rateValue;

    MeetingCallRate(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getRateValue() {
        return this.rateValue;
    }
}
