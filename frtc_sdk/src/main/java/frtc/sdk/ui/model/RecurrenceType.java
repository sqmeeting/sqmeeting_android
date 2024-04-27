package frtc.sdk.ui.model;

public enum RecurrenceType {
    NO("NO"),
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY")
    ;

    private String typeName;

    RecurrenceType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }
}
