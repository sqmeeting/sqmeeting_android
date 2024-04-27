package frtc.sdk.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.util.AttributeSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class TimerView extends android.support.v7.widget.AppCompatTextView {

    private static String TAG = TimerView.class.getSimpleName();
    private Context context;
    private final String DEFAULT_FORMAT = "aa H:mm";
    private SimpleDateFormat form;

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setLocale();
    }

    public void setLocale() {
        Configuration config = getResources().getConfiguration();
        form = new SimpleDateFormat(DEFAULT_FORMAT, config.locale);
        setFormatText();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterTimeChangedReceiver();
    }

    private void unregisterTimeChangedReceiver() {
        context.unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)){
                form.setTimeZone(TimeZone.getDefault());
            }
            setFormatText();
        }
    };

    private void setFormatText() {
        setText(form.format(new Date()));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerTimeChangedReceiver();
        setFormatText();
    }

    private void registerTimeChangedReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(receiver, intentFilter, null, getHandler());
    }
}
