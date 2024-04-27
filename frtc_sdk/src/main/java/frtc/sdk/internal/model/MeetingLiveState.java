package frtc.sdk.internal.model;

public enum MeetingLiveState {
    STARTED("STARTED"),
    NOT_STARTED("NOT_STARTED"),
    ;

    private String name;

    MeetingLiveState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
