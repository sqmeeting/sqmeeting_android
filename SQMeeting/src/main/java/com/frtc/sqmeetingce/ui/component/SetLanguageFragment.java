package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.util.LanguageUtil;
import frtc.sdk.util.SettingUtil;

public class SetLanguageFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();
    public MainActivity mActivity;

    private ListView mListView;
    private TextView languageTitle;
    private String[] languages;
    private Button buttonComplete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.set_language_fragment, container, false);
        init(view);
        initData();
        return view;
    }

    private void init(View view) {
        mListView = view.findViewById(R.id.listView);
        languageTitle = view.findViewById(R.id.language_title);
        buttonComplete = view.findViewById(R.id.button_complete);
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
        });
        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
        });

    }

    private void initData() {
        languages = getResources().getStringArray(R.array.language_type);
        LanguageSingleAdapter mAdapter = new LanguageSingleAdapter(
                mActivity, languages, SettingUtil.getInstance(mActivity.getApplicationContext())
                .getLanguage(), pos -> {
                    if (pos == SettingUtil.getInstance(mActivity.getApplicationContext()).getLanguage()) {
                        return;
                    }
                    SettingUtil.getInstance(mActivity.getApplicationContext()).saveLanguage(pos);

                    LanguageUtil.setLanguage(mActivity);
                    languageTitle.setText(mActivity.getResources().getString(R.string.language_setting_title));
                    buttonComplete.setText(mActivity.getResources().getString(R.string.language_setting_done));
                });
        mListView.setAdapter(mAdapter);
    }


    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
    }

}
