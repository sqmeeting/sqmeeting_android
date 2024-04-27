package frtc.sdk.ui.model;

public enum MuteUponEntry {

    ENABLE("ENABLE"),
    DISABLE("DISABLE");

    String ability;

    MuteUponEntry(String ability){
        this.ability = ability;
    }

    public String getValue(){
        return this.ability;
    }
}
