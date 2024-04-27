package frtc.sdk.internal.model;

public enum InboundMessage {
    register("register", 0x00010000),
    unregister("unregister", 0x00010001),
    joinMeetingPrepare("joinMeetingPrepare", 0x00001000),
    joinMeeting("joinMeeting", 0x00001001),
    leaveMeeting("leaveMeeting", 0x00001002),
    setLocalPeopleAudio("setLocalPeopleAudio", 0x00002000),
    setLocalPeopleVideo("setLocalPeopleVideo", 0x00002001),
    setRemotePeopleVideo("setRemotePeopleVideo", 0x00003000),
    inputPassword("inputPassword", 0x00004000),
    setNoiseBlock("setNoiseBlock", 0x00005000),
    mediaStatsReq("mediaStatsReq", 0x00006000),
    mediaStatsResp("mediaStatsResp", 0x00006001),

    startShareContent("startShareContent", 0x00007000),
    stopShareContent("stopShareContent", 0x00007001),

    getVersion("getVersion", 0x00008000),

    startTimer("startTimer", 0x00009000),
    stopTimer("stopTimer", 0x00009001),

    startUploadLogs("startUploadLogs", 0x0000a000),
    getUploadLogsStatus("getUploadLogsStatus", 0x0000a001),
    cancelUploadLogs("cancelUploadLogs", 0x0000a002),

    setAudioAndVideo("setAudioAndVideo", 0x0000b000),

    ;

    private String name;
    private int code;

    InboundMessage(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static InboundMessage from(int code) {
        for (InboundMessage type : values()) {
            if (type.code == code){
                return type;
            }
        }
        return null;
    }
}
