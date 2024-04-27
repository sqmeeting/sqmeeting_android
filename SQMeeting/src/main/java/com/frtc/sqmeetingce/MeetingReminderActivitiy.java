package com.frtc.sqmeetingce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import frtc.sdk.log.Log;
import frtc.sdk.util.ActivityUtils;


public class MeetingReminderActivitiy extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        if(ActivityUtils.isExistMeetingActivity()){
            finish();
            return;
        }
        Intent intent = getIntent();
        String strMeetings = intent.getStringExtra("scheduleMeeting");
        Log.i(TAG, " strMeetings = "+strMeetings);

        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("scheduleMeeting",strMeetings);
        intent.putExtra("isClickNotification",true);
        startActivity(intent);
        finish();
       

    }



    @Override
    public void onDestroy(){
        super.onDestroy();

    }



}
