package frtc.sdk.internal.model;

public enum MeetingErrorCode {
    HangUp(-1),
    StatusOk(0),
    MeetingEndAbnormal(3),
    MeetingExpired(4),
    MeetingFull(5),
    MeetingLocked(7),
    MeetingNoExist(8),
    MeetingNoStarted(9),
    MeetingStop(10),
    AuthenticationFail(11),
    PasscodeTooManyRetries(14),
    GuestUnallowed(15),
    LicenseLimitReached(17),
    LicenseNoFound(18),
    RemovedFromMeeting(19),
    ServerError(20),
    Unknown(22),
    ;

    private int code;

    MeetingErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MeetingErrorCode from(int code) {
        for (MeetingErrorCode type : values()) {
            if (type.code == code){
                return type;
            }
        }
        return Unknown;
    }
}
