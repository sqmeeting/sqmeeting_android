package frtc.sdk.internal.service;

import frtc.sdk.internal.model.MeetingConfig;
import frtc.sdk.internal.model.MeetingStatusInfo;

public interface IMeeting {
    MeetingStatusInfo getMeetingInfo();
    MeetingConfig getMeetingConfig();
}
