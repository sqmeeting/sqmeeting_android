package frtc.sdk.internal.model;

public class MeetingMediaStatsWrapper {
    public MeetingMediaStats mediaStatistics;
    public MeetingSignalStats signalStatistics;

    public MeetingMediaStats getMediaStatistics() {
        return mediaStatistics;
    }

    public void setMediaStatistics(MeetingMediaStats mediaStatistics) {
        this.mediaStatistics = mediaStatistics;
    }

    public MeetingSignalStats getSignalStatistics() {
        return signalStatistics;
    }

    public void setSignalStatistics(MeetingSignalStats signalStatistics) {
        this.signalStatistics = signalStatistics;
    }

}