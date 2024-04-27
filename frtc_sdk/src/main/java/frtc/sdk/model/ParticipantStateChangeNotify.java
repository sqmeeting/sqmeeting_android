package frtc.sdk.model;

import java.util.List;

public class ParticipantStateChangeNotify {
    private List<Participant> participantList;
    private boolean isFullList;
    private String selfNewName;

    public List<Participant> getParticipantList() {
        return participantList;
    }

    public void setParticipantList(List<Participant> participantList) {
        this.participantList = participantList;
    }

    public boolean isFullList() {
        return isFullList;
    }

    public void setFullList(boolean fullList) {
        isFullList = fullList;
    }

    public String getSelfNewName() {
        return selfNewName;
    }

    public void setSelfNewName(String selfNewName) {
        this.selfNewName = selfNewName;
    }
}
