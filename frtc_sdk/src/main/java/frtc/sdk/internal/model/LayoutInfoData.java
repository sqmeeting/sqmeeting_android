package frtc.sdk.internal.model;

import java.util.List;

public class LayoutInfoData {
    private String layout_mode;
    private int total_count;
    private LayoutData layout_content;
    private List<LayoutData> layout;
    private String active_speaker_msid;
    private String pin_speaker_uuid;

    public String getLayout_mode() {
        return layout_mode;
    }

    public void setLayout_mode(String layout_mode) {
        this.layout_mode = layout_mode;
    }

    public String getActive_speaker_msid() {
        return active_speaker_msid;
    }

    public void setActive_speaker_msid(String active_speaker_msid) {
        this.active_speaker_msid = active_speaker_msid;
    }

    public String getPinUuId() {
        return pin_speaker_uuid;
    }

    public void setPinUuid(String pinUuid) {
        this.pin_speaker_uuid = pinUuid;
    }


    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public LayoutData getLayout_content() {
        return layout_content;
    }

    public void setLayout_content(LayoutData layout_content) {
        this.layout_content = layout_content;
    }

    public List<LayoutData> getLayout() {
        return layout;
    }

    public void setLayout(List<LayoutData> layout) {
        this.layout = layout;
    }

}
