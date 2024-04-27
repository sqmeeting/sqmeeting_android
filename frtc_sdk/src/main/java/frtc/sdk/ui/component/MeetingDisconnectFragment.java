
package frtc.sdk.ui.component;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.log.Log;

public class MeetingDisconnectFragment extends Fragment {

    private static final String TAG = MeetingDisconnectFragment.class.getSimpleName();

    private String message;
    private TextView tv;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_disconnecting_notice_fragment, container, false);
        tv = view.findViewById(R.id.message);
        tv.setText(message);
        return view;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        try {
            this.message = (String) args.get(FrtcFragmentManager.MESSAGE);
            if (tv != null) {
                tv.setText(message);
            }
        } catch (Exception e) {
            Log.e(TAG, "setArguments: " + e.getMessage());
        }
    }

}

