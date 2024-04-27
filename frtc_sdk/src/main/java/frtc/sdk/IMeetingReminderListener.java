package frtc.sdk;

import frtc.sdk.model.ScheduledMeeting;

public interface IMeetingReminderListener {
    void onIgnoreCallback();

    void onJoinMeetingCallback(ScheduledMeeting scheduledMeeting);
}
