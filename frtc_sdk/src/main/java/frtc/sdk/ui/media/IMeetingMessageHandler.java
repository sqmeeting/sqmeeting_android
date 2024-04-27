package frtc.sdk.ui.media;

import frtc.sdk.internal.model.LayoutInfoData;

public interface IMeetingMessageHandler {
    void onLayoutInfoNotify(LayoutInfoData layoutData);
    void onRequestPeopleVideo(String msid, int w, int h);
    void onStopPeopleVideo(String msid);
    void onAddPeopleVideo(String msid, int w, int h);
    void onDeletePeopleVideo(String msid);
    void onContentSendRefused();
}
