package frtc.sdk.util;

import android.content.Context;
import android.media.MediaPlayer;

import frtc.sdk.R;
import frtc.sdk.log.Log;

public class AudioRawUtil {

    private static AudioRawUtil instance = null;
    private static MediaPlayer player = null;

    private AudioRawUtil(Context context) {
        if (player == null) {
            player = MediaPlayer.create(context, R.raw.connecting);
        }
    }

    public static AudioRawUtil getInstance(Context context) {
        if (instance == null) {
            instance = new AudioRawUtil(context);
        }
        return instance;
    }

    public static void release(){
        instance = null;
    }

    public void stop(){
        if (player != null) {
            try {
                if(player.isPlaying()){
                    player.stop();
                }
                player.release();
                player = null;
            } catch (Exception e) {
                Log.e("AudioRawUtil", "Audio player stop Exception", e);
            }
        }
    }
    public void playback(){
        if (player != null) {
            try {
                player.setLooping(true);
                player.start();
            } catch (Exception e) {
                Log.e("AudioRawUtil","playback error",e);
                stop();
            }
        }
    }
}