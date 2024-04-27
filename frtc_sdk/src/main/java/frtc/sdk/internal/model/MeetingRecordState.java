package frtc.sdk.internal.model;

public enum MeetingRecordState {
    STARTED("STARTED"),
    NOT_STARTED("NOT_STARTED"),
    ;

    private String name;

    MeetingRecordState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
