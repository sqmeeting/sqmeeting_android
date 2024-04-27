package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.frtc.sqmeetingce.R;


public class ConnectingFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_connecting, container, false);
        setViewAnimation(view);
        return view;
    }

    private void setViewAnimation(View view){
        ImageView rotateImage = view.findViewById(R.id.rotate_image);
        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotateImage.setAnimation(operatingAnim);
    }
}
