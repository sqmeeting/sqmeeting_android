package frtc.sdk.ui.model;

public enum CameraMessageType {
    Open(0x1000),
    Close(0x1001),
    StartPreview(0x1010),
    StopPreview(0x1011),
    StartRecord(0x1020),
    StopRecord(0x1021),
    SwitchCamera(0x1050),
    Rotation(0x1060);

    private int code;

    CameraMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CameraMessageType from(int code) {
        for (CameraMessageType type : values()) {
            if (type.code == code){
                return type;
            }
        }
        return null;
    }
}
