package com.frtc.sqmeetingce.ui.component;

public enum FragmentTagEnum {

    FRAGMENT_HOME("FRAGMENT_HOME"),
    FRAGMENT_USER_SETTINGS("FRAGMENT_USER_SETTINGS"),
    FRAGMENT_JOIN_MEETING("FRAGMENT_JOIN_MEETING"),
    FRAGMENT_CREATE_MEETING("FRAGMENT_CREATE_MEETING"),
    FRAGMENT_SIGN_IN("FRAGMENT_SIGN_IN"),
    FRAGMENT_CONNECTING("CONNECTING_FRAGMENT"),
    FRAGMENT_SET_SERVER("FRAGMENT_SET_SERVER"),
    FRAGMENT_USER("FRAGMENT_USER"),
    FRAGMENT_MEETING_DETAILS("FRAGMENT_MEETING_DETAILS"),
    FRAGMENT_SCHEDULE_MEETING_DETAILS("FRAGMENT_SCHEDULE_MEETING_DETAILS"),
    FRAGMENT_SCHEDULE_MEETING("FRAGMENT_SCHEDULE_MEETING"),
    FRAGMENT_UPDATE_SCHEDULED_MEETING("FRAGMENT_UPDATE_SCHEDULED_MEETING"),
    FRAGMENT_SCHEDULE_MEETING_RATE("FRAGMENT_SCHEDULE_MEETING_RATE"),
    FRAGMENT_SCHEDULE_MEETING_JOIN_TIME("FRAGMENT_SCHEDULE_MEETING_JOIN_TIME"),
    FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ("FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ"),
    FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ_SETTING("FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ_SETTING"),
    SCHEDULE_RECURRENCE_MEETING_LIST_FRAGMENT("SCHEDULE_RECURRENCE_MEETING_LIST_FRAGMENT"),
    FRAGMENT_EDIT_SINGLE_RECURRENCE_MEETING("FRAGMENT_EDIT_SINGLE_RECURRENCE_MEETING"),
    FRAGMENT_INVITED_USER("FRAGMENT_INVITED_USER"),
    FRAGMENT_USER_CHANGE_PASSWORD("FRAGMENT_USER_CHANGE_PASSWORD"),
    FRAGMENT_SET_LANGUAGE("FRAGMENT_SET_LANGUAGE"),
    FRAGMENT_PROBLEM_DIAGNOSIS("FRAGMENT_PROBLEM_DIAGNOSIS"),
    FRAGMENT_UPLOAD_LOG("FRAGMENT_UPLOAD_LOG");

    private String typeName;

    FragmentTagEnum(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

}