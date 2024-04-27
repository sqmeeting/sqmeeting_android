package frtc.sdk.ui.component;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import frtc.sdk.R;
import frtc.sdk.ui.model.FragmentType;

public class FrtcFragmentManager {

    public final static String MESSAGE = "MESSAGE";
    public final static String INPUT_COUNT = "INPUT_COUNT";
    private FragmentType curFragmentType = FragmentType.IDLE;
    private ViewGroup viewGroup;
    private FragmentManager manager;
    private Fragment currentFragment;


    private Bundle meetingInfo;
    private Bundle disconnectingInfo;

    private boolean meetingPaused;
    private int passwordInputNum;

    public FrtcFragmentManager(ViewGroup fragContainer, FragmentManager manager) {
        this.viewGroup = fragContainer;
        this.manager = manager;
        passwordInputNum = 0;
    }

    public void resume() {
        meetingPaused = false;
        updateFragment();
    }

    public void pause() {
        meetingPaused = true;
    }

    public Fragment getCurFragment() {
        return currentFragment;
    }
    public void switchToConnectedFragment() {
        setFragment(FragmentType.CONNECTED);
    }
    public void switchToDisconnectingFragment(Bundle bundle) {
        this.disconnectingInfo = bundle;
        setFragment(FragmentType.DISCONNECTING);
    }

    public void switchToConnectingFragment(Bundle bundle) {
        this.meetingInfo = bundle;
        setFragment(FragmentType.CONNECTING);
    }

    public void switchToPasswordFragment() {
        setFragment(FragmentType.PASSWORD);
    }
    private void setFragment(FragmentType type) {
        if (meetingPaused) {
            curFragmentType = type;
            return;
        }
        if (type == curFragmentType) {
            return;
        }
        curFragmentType = type;
        updateFragment();
    }

    private void updateFragment() {
        viewGroup.setVisibility(View.VISIBLE);
        Fragment nFragment = null;
        if (curFragmentType != null) {
            switch (curFragmentType) {
                case CONNECTING:
                    nFragment = new MeetingConnectingFragment();
                    nFragment.setArguments(meetingInfo);
                    break;
                case PASSWORD:
                    nFragment = new PasswordInputFragment();
                    Bundle pwdBundle = new Bundle();
                    pwdBundle.putInt(INPUT_COUNT, passwordInputNum);
                    nFragment.setArguments(pwdBundle);
                    passwordInputNum++;
                    break;
                case CONNECTED:
                    removeAllFragment();
                    return;
                case DISCONNECTING:
                    nFragment = new MeetingDisconnectFragment();
                    Bundle b = new Bundle();
                    if (disconnectingInfo != null && disconnectingInfo.containsKey(MESSAGE)) {
                        b.putString(MESSAGE, disconnectingInfo.getString(MESSAGE));
                    }
                    nFragment.setArguments(b);
                    break;
                default:
                    break;
            }
        }
        if (nFragment != null) {
            FragmentTransaction t = manager.beginTransaction();
            t.replace(R.id.meeting_state_manager, nFragment);
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            t.commitAllowingStateLoss();
            currentFragment = nFragment;
            curFragmentType = FragmentType.IDLE;
        }
    }

    public void removeAllFragment() {
        if (!meetingPaused && currentFragment != null) {
            FragmentTransaction t = manager.beginTransaction();
            t.remove(currentFragment);
            t.commitAllowingStateLoss();
            currentFragment = null;
        }
        viewGroup.setVisibility(View.INVISIBLE);
    }



}
