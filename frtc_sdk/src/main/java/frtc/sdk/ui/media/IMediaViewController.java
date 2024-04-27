package frtc.sdk.ui.media;

import android.view.View;
import android.view.ViewGroup;

public interface IMediaViewController {

    View getMediaView();
    String getDisplayName();
    void setDisplayName(String displayName);
    void hideDisplayNameTextView();
    String getMsid();
    void setMsid(String msid);
    void setView_uuid(String uuid);
    String getView_uuid();
    void destroy(ViewGroup parent);
    void setActiveSpeaker(boolean isActiveSpeaker);

    void setContentWatermarkVisible(boolean visible, String name);

    void setLoading(boolean loading);
    void setPinned(boolean pinned);
    void setAudioMuted(boolean muted);

    void reqRender();
    void enableRender(boolean enabled);
    boolean isRenderEnabled();

    boolean isViewVisible();
    void setViewVisible(boolean visible);

}
