package frtc.sdk;

import java.util.ArrayList;

import frtc.sdk.internal.model.UploadLogsStatus;
import frtc.sdk.model.ScheduledMeeting;

public interface IFrtcCommonListener {

    void onTimerEvent(int timerId);

    void onUploadLogsNotify(int tractionId);
    void onUploadLogsStatusNotify(UploadLogsStatus uploadLogsStatus);
    void onCancelUploadLogsNotify();

    void onSaveMeetingIntoMeetingListNotify();
    void onShowMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders);
}
