package frtc.sdk.internal.model;

public enum InternalMessageType {

    joinMeeting("JoinMeeting", 0x0010_0000),
    endMeeting("EndMeeting", 0x0010_0002),
    verifyMeetingPassword("VerifyPasscode", 0x0014_0000),
    startShareContent("StartSendContent", 0x0025_0000),
    stopShareContent("StopSendContent", 0x0025_0001),
    getRTCVersion("GetRTCVersion", 0x0026_0000),
    setNoiseBlock("EnableNoiseBlock", 0x0027_0000),
    setAudioAndVideo("ReportMuteStatus", 0x0028_0000),
    setLocalPeopleAudio("MuteLocalAudio", 0x0028_0001),
    setLocalPeopleVideo("MuteLocalVideo", 0x0030_0000),
    setRemotePeopleVideo("MuteRemoteVideo", 0x0030_0001),
    startTimer("StartTimer", 0x0033_0000),
    stopTimer("StopTimer", 0x0033_0001),
    onStartUploadLogs("StartUploadLogs", 0x0031_0000),
    onGetUploadLogsStatus("GetUploadStatus", 0x0031_0001),
    onCancelUploadLogs("CancelUploadLogs", 0x0031_0002),
    temperatureInfoNotify("BatteryOverHeat", 0x0032_0000),
    getMediaStats("GetStatistics", 0x0029_0000),

    onMeetingJoinInfo("OnMeetingJoinInfo", 0x0010_0001),
    onMeetingStatusChange("OnMeetingStatusChange", 0x0011_0000),
    onMeetingPasswordErrorNotify("OnPasscodeReject", 0x0014_0001),
    onMeetingPasswordRequireNotify("OnPasscodeRequest", 0x0014_0002),
    onMeetingSessionInfoNotify("OnMeetingSessionStatus", 0x0018_0002),

    onLayoutChange("OnLayoutChange", 0x0012_0000),
    onLayoutSetting("OnLayoutSetting", 0x0013_0000),
    onRequestAudioNotify("OnRequestAudioStream", 0x0015_0001),
    onAddAudioNotify("OnAddAudioStream", 0x0015_0002),
    onStopAudioNotify("OnStopAudioStream", 0x0015_0003),
    onMicrophonePermissionNotify("OnMuteLock", 0x0017_0000),

    onContentStatusChangeNotify("OnContentStatusChange", 0x0018_0000),
    onShareContentFailNotify("OnContentFailForLowBandwidth", 0x0018_0001),
    onParticipantCountNotify("OnParticipantCount", 0x0019_0000),
    onParticipantStateChangeNotify("OnParticipantStatusChange", 0x0019_0001),

    onTextOverlayNotify("OnTextOverlay", 0x0020_0000),

    onUnmuteRequest("OnUnmuteRequest", 0x0022_0000),
    onUnmuteAllowNotify("OnUnmuteAllow", 0x0022_0001),
    onTimer("OnTimer", 0x0023_0000),
    onNetworkStatusNotify("OnNetworkStatusChange", 0x0024_0000),


    onRequestPeopleVideoNotify("OnRequestVideoStream", 0x0016_0000),
    onStopPeopleVideoNotify("OnStopVideoStream", 0x0016_0001),
    onAddPeopleVideoNotify("OnAddVideoStream", 0x0016_0002),
    onDeletePeopleVideoNotify("OnDeleteVideoStream", 0x0016_0003),

    ;
    private String name;
    private int code;

    InternalMessageType(String name, int code){
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public static InternalMessageType from(int code) {
        for (InternalMessageType type : values()) {
            if (type.code == code){
                return type;
            }
        }
        return null;
    }

    public static InternalMessageType from(String name) {
        for (InternalMessageType type : values()) {
            if (type.name.equalsIgnoreCase(name)){
                return type;
            }
        }
        return null;
    }

    public String toString() {
        return name;
    }
}
