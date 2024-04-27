package frtc.sdk.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import frtc.sdk.R;
import frtc.sdk.log.Log;

public class ShareScreenNoticeService extends Service {


    private static final String TAG = ShareScreenNoticeService.class.getSimpleName();

    private String NOTIFICATION_ID="com.frtc.sqmeetingce";

    @Override
    public void onCreate() {
        super.onCreate();
       Log.d(TAG,"onCreate");
        startNotification();
    }

    private void startNotification(){
        Log.d(TAG,"startNotification:");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(this, ShareScreenNoticeService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.local_content_cell_title))
                    .setContentIntent(pendingIntent);
            Notification notification = notificationBuilder.build();
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            startForeground(1, notification);
        }
    }

    @Override
    public  int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG,"onStartCommand:"+intent+","+flags+","+startId);
        return super.onStartCommand(intent,flags,startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        stopForeground(true);
        super.onDestroy();

    }
}
