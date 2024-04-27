package frtc.sdk.ui.model;

public class MediaData {
    private MediaType mediaType = MediaType.UNKNOWN;
    private String msid;
    private int width;
    private int height;

    public MediaData(){

    }
    public MediaData(MediaType mediaType, String msid){
        this.mediaType = mediaType;
        this.msid = msid;
    }

    public String getMsid() {
        return msid;
    }

    public void setMsid(String msid) {
        this.msid = msid;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
