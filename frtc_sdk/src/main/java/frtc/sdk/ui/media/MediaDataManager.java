package frtc.sdk.ui.media;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import frtc.sdk.ui.layout.IMediaDataListener;
import frtc.sdk.ui.model.MediaData;
import frtc.sdk.ui.model.MediaType;


public class MediaDataManager {

    private static final String TAG = MediaDataManager.class.getSimpleName();

    private MediaData localContent = null;
    private final Map<String, MediaData> peopleMap = new ConcurrentHashMap<>();
    private final Map<String, MediaData> contentMap = new ConcurrentHashMap<>();

    private IMediaDataListener mediaViewListener;

    public MediaDataManager(IMediaDataListener mediaViewListener) {
        this.mediaViewListener = mediaViewListener;
    }

    public boolean isContentMediaData(String msid) {
        return msid.startsWith(MediaType.LOCAL_CONTENT.getValue()) || msid.startsWith(MediaType.REMOTE_CONTENT.getValue());
    }

    public boolean isLocalContent(String msid){
        return msid.startsWith(MediaType.LOCAL_CONTENT.getValue());
    }
    public boolean isRemoteContent(String msid){
        return msid.startsWith(MediaType.REMOTE_CONTENT.getValue());
    }

    public boolean isRemotePeople(String msid){
        return msid.startsWith(MediaType.REMOTE_PEOPLE.getValue());
    }

    public MediaData getPeople(String msid){
        return peopleMap.get(msid);
    }

    public MediaData getContent(String msid){
        return contentMap.get(msid);
    }

    public void requestPeopleVideo(String msid, int w, int h) {
        if (mediaViewListener != null) {
            if (isLocalContent(msid)) {
                if (localContent == null) {
                    localContent = initLocalContentMediaData(msid,w,h);
                    mediaViewListener.initializeContentMediaData(msid, w,h);
                    mediaViewListener.startSendContent();
                } else {
                    updateLocalContentMediaData(msid,w,h);
                    mediaViewListener.updateLocalContentMediaData(msid,w,h);
                }
            } else {
                mediaViewListener.addLocalPeople(msid, 1280, 720);
            }
        }
    }

    public void changePeopleVideo(String msid, int w, int h){
        if (getPeople(msid) != null || getContent(msid) != null) {
            updatePeopleVideo(msid, w, h);
        } else {
            addPeopleVideo(msid, w, h);
        }
    }

    public void stopPeopleVideo(String msid) {
        if (mediaViewListener != null) {
            if (msid.startsWith(MediaType.REMOTE_CONTENT.getValue())) {
                contentMap.remove(msid);
                mediaViewListener.removeContentVideo(msid);
            } else if (msid.startsWith(MediaType.REMOTE_PEOPLE.getValue())) {
                peopleMap.remove(msid);
                mediaViewListener.removePeopleVideo(msid);
            } else if (msid.startsWith(MediaType.LOCAL_PEOPLE.getValue())){
                mediaViewListener.removeLocalPeople(msid);
            }
        }
    }

    public void releaseLocalContentMediaData(){
        localContent = null;
    }

    private void addPeopleVideo(String msid, int w, int h) {
        if (mediaViewListener != null) {
            if (isContentMediaData(msid)) {
                if(isRemoteContent(msid)) {
                    contentMap.put(msid, new MediaData(MediaType.REMOTE_CONTENT, msid));
                }
                mediaViewListener.addContentVideo(msid, w, h);
            } else if (isRemotePeople(msid)) {
                peopleMap.put(msid, new MediaData(MediaType.REMOTE_PEOPLE, msid));
                mediaViewListener.addPeopleVideo(msid, w, h);
            }
        }
    }

    private void updatePeopleVideo(String msid, int w, int h) {
        if (mediaViewListener != null) {
            if (isContentMediaData(msid)) {
                mediaViewListener.updateContentVideo(msid, w, h);
            } else {
                mediaViewListener.updatePeopleVideo(msid, w, h);
            }
        }
    }

    private MediaData initLocalContentMediaData(String msid, int w, int h){
        MediaData data = new MediaData();
        data.setMsid(msid);
        data.setMediaType(MediaType.LOCAL_CONTENT);
        data.setWidth(w);
        data.setHeight(h);
        return data;
    }

    private void updateLocalContentMediaData(String msid, int w, int h){
        if(localContent != null){
            localContent.setMsid(msid);
            localContent.setMediaType(MediaType.LOCAL_CONTENT);
            localContent.setWidth(w);
            localContent.setHeight(h);
        }
    }

}
