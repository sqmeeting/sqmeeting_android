package frtc.sdk.ui.layout;

public interface IMediaDataListener {
    void initializeContentMediaData(String msid, int w, int h);
    void updateLocalContentMediaData(String msid, int w, int h);
    void startSendContent();
    void stopSendContent();
    void addPeopleVideo(String msid, int w, int h);
    void removePeopleVideo(String msid);
    void updatePeopleVideo(String msid, int w, int h);
    void addContentVideo(String msid, int w, int h);
    void addLocalPeople(String msid, int w, int h);
    void removeLocalPeople(String msid);
    void removeContentVideo(String msid);
    void updateContentVideo(String msid, int w, int h);

}
