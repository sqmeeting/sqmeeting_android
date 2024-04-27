package frtc.sdk.internal.model;

import java.util.ArrayList;
import java.util.List;

public class ParticipantStateInfo {

    private List<ParticipantInfo> parti_status_change;
    private boolean full_list;

    public List<ParticipantInfo> getParti_status_change() {
        return parti_status_change;
    }

    public void setParti_status_change(List<ParticipantInfo> parti_status_change) {
        this.parti_status_change = parti_status_change;
    }

    public boolean getFull_list() {
        return full_list;
    }

    public void setFull_list(boolean full_list) {
        this.full_list = full_list;
    }

    public void addLocal2ParticipantList(String myDisplayName, String clientId, String userId) {
        List<ParticipantInfo> newParticipantList = new ArrayList<>();
        ParticipantInfo myself = new ParticipantInfo();
        myself.setDisplay_name(myDisplayName);
        myself.setUuid(clientId);
        myself.setAudioMuted(true);
        myself.setVideoMuted(true);
        myself.setUser_id(userId);
        newParticipantList.add(myself);
        if(parti_status_change != null && !parti_status_change.isEmpty()) {
            newParticipantList.addAll(parti_status_change);
        }
        parti_status_change = newParticipantList;
    }

}
