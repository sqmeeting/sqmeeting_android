package frtc.sdk.internal.model;

public enum MeetingContentState {
    idle("kContentIdle"),
    contentSending("kContentSending"),
    contentReceiving("kContentReceving"),

    ;

    private String name;

    MeetingContentState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
