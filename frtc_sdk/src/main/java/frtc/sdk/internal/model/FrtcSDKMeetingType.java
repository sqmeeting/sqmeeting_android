package frtc.sdk.internal.model;

public enum FrtcSDKMeetingType {
    INSTANT("instant"),
    RESERVATION("reservation"),
    RECURRENCE("recurrence")
    ;

    private String typeName;

    FrtcSDKMeetingType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }
}
