package frtc.sdk.ui.component;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import frtc.sdk.R;
import frtc.sdk.util.AudioRawUtil;
import frtc.sdk.util.Constants;

public class MeetingConnectingFragment extends Fragment {

    private final Handler handler = new Handler();
    private JoinMeetingFailedListener listener;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            listener.autoLeaveMeeting();
        }
    };

    @Override
    public void onDestroyView() {
        AudioRawUtil.getInstance(getContext()).stop();
        handler.removeCallbacks(runnable);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_connecting_fragment, container, false);
        Button button = view.findViewById(R.id.leave_meeting_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.autoLeaveMeeting();
                }
            }
        });
        button.requestFocus();

        AudioRawUtil.getInstance(getContext()).playback();
        handler.postDelayed(runnable, Constants.DELAY);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (JoinMeetingFailedListener) activity;
    }
}
