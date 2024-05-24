package com.frtc.sqmeetingce.ui.component;

public class FragmentFactory {

    public static BaseFragment createFragment(FragmentTagEnum tag){
        BaseFragment fragment = null;
        switch (tag) {
            case FRAGMENT_HOME:
                fragment = new HomeFragment();
                break;
            case FRAGMENT_USER_SETTINGS:
                fragment = new UserSettingsFragment();
                break;
            case FRAGMENT_SET_SERVER:
                fragment = new SetServerFragment();
                break;
            case FRAGMENT_JOIN_MEETING:
                fragment = new JoinMeetingFragment();
                break;
            case FRAGMENT_CREATE_MEETING:
                fragment = new CreateMeetingFragment();
                break;
            case FRAGMENT_SIGN_IN:
                fragment = new SignInFragment();
                break;
            case FRAGMENT_SCHEDULE_MEETING:
                fragment = new ScheduleMeetingFragment();
                break;
            case FRAGMENT_UPDATE_SCHEDULED_MEETING:
                fragment = new UpdateScheduledMeetingFragment();
                break;
            case FRAGMENT_SCHEDULE_MEETING_RATE:
                fragment = new ScheduleMeetingRateFragment();
                break;
            case FRAGMENT_SCHEDULE_MEETING_JOIN_TIME:
                fragment = new ScheduleMeetingJoinTimeFragment();
                break;
            case FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ:
                fragment = new ScheduleMeetingRepetitionFreqFragment();
                break;
            case FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ_SETTING:
                fragment = new ScheduleMeetingRepetitionFreqSettingFragment();
                break;
            case FRAGMENT_SCHEDULE_RECURRENCE_MEETING_LIST:
                fragment = new ScheduleRecurrenceMeetingListFragment();
                break;
            case FRAGMENT_EDIT_SINGLE_RECURRENCE_MEETING:
                fragment = new EditSingleRecurrenceMeetingFragment();
                break;
            case FRAGMENT_SCHEDULE_MEETING_DETAILS:
                fragment = new ScheduledMeetingDetailsFragment();
                break;
            case FRAGMENT_INVITED_USER:
                fragment = new InvitedUserFragment();
                break;
            case FRAGMENT_USER:
                fragment = new UserFragment();
                break;
            case FRAGMENT_USER_CHANGE_PASSWORD:
                fragment = new UserChangePasswordFragment();
                break;
            case FRAGMENT_MEETING_DETAILS:
                fragment = new MeetingDetailsFragment();
                break;
            case FRAGMENT_CONNECTING:
                fragment = new ConnectingFragment();
                break;
            case FRAGMENT_SET_LANGUAGE:
                fragment = new SetLanguageFragment();
                break;
            case FRAGMENT_PROBLEM_DIAGNOSIS:
                fragment = new ProblemDiagnosisFragment();
                break;
            case FRAGMENT_UPLOAD_LOG:
                fragment = new UploadLogFragment();
                break;
            case FRAGMENT_USER_RECORDING:
                fragment = new UserRecordingFragment();
                break;
            default:
                break;
        }
        return fragment;
    }
}
