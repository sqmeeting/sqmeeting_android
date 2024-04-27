package frtc.sdk.ui.model;

public enum WatermarkType {

    SINGLE("single");

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    WatermarkType(String typeName) {
        this.typeName = typeName;
    }
}
