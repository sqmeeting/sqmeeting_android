package frtc.sdk.internal.model;

public enum VideoColorType {
    kNoType("UnknownType",0),
    kABGR("ABGR",3),
    kNV21("NV21",10);

    private String name;
    private int code;
    VideoColorType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public int getCode() {
        return code;
    }

}
