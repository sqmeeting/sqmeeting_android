package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.R;
import frtc.sdk.model.UnmuteRequest;
import frtc.sdk.ui.component.UnmuteRequestParticipantListAdapter;
import frtc.sdk.util.BaseDialog;


public class PermissionsRequestListDlg extends BaseDialog {

    private static final String TAG = PermissionsRequestListDlg.class.getSimpleName();
    private Context mContext;
    private ListView mListView;
    private UnmuteRequestParticipantListAdapter mAdapter;
    private List<UnmuteRequest> unmuteRequestParticipants = new ArrayList<>();
    private ConstraintLayout noPermissionsReqLayout;

    private Button agreeAllBtn;
    private IMeetingControlDlgListener mListener;


    public PermissionsRequestListDlg(Context context, List<UnmuteRequest> data) {
        super(context, R.style.NoMaskDialogTheme);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        unmuteRequestParticipants.clear();
        unmuteRequestParticipants.addAll(data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.permissions_list_layout);

        ImageView backView = findViewById(R.id.back_button);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSetUnviewGone();
                dismiss();
            }
        });

        noPermissionsReqLayout = findViewById(R.id.no_permissions_req_layout);
        mListView = findViewById(R.id.permission_list_view);
        mListView.setFooterDividersEnabled(true);

        if(unmuteRequestParticipants != null && unmuteRequestParticipants.size() > 0) {
            noPermissionsReqLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }else {
            noPermissionsReqLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }

        mAdapter = new UnmuteRequestParticipantListAdapter(getContext(), R.layout.unmute_request_item, unmuteRequestParticipants);
        mAdapter.setOnClickViewListener(new UnmuteRequestParticipantListAdapter.ClickViewListener() {
            @Override
            public void onClick(int position, UnmuteRequest unmteReq) {
                if(mListener != null){
                    List<UnmuteRequest> unmuteRequestList = new ArrayList<>();
                    unmuteRequestList.add(unmteReq);
                    mListener.onAllowUnmute(unmuteRequestList);
                }
            }

        });

        mListView.setAdapter(mAdapter);

        agreeAllBtn = findViewById(R.id.agree_all_btn);
        agreeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onAllowUnmute(unmuteRequestParticipants);
                }
            }
        });
    }

    public void updateNoPermissionsVisible(){
        if(unmuteRequestParticipants.isEmpty()){
            noPermissionsReqLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            agreeAllBtn.setAlpha((float) 0.3);
            agreeAllBtn.setClickable(false);
        }else{
            noPermissionsReqLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            agreeAllBtn.setAlpha((float) 1.0);
            agreeAllBtn.setClickable(true);
        }
    }


    public void refreshAdapter(List<UnmuteRequest> infos) {
        unmuteRequestParticipants.clear();
        unmuteRequestParticipants.addAll(infos);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnDialogListener(IMeetingControlDlgListener listener) {
        mListener = listener;
    }
}
