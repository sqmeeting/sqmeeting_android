package frtc.sdk.ui.model;

import frtc.sdk.internal.model.MediaStatsInfo;

public class MeetingStatsInfo {

    String mediaType;
    MediaStatsInfo mediaStatsInfo;

    public MeetingStatsInfo(String mediaType, MediaStatsInfo mediaStatsInfo){
        this.mediaType = mediaType;
        this.mediaStatsInfo = mediaStatsInfo;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public MediaStatsInfo getMediaStatsInfo() {
        return mediaStatsInfo;
    }

    public void setMediaStatsInfo(MediaStatsInfo mediaStatsInfo) {
        this.mediaStatsInfo = mediaStatsInfo;
    }
}
