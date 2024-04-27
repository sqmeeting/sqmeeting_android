package frtc.sdk.log;

import android.content.Context;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

public class CrashHandler implements Thread.UncaughtExceptionHandler{
    private static final String TAG = "CrashHandler";

    private static CrashHandler instance;
    private Context context;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public synchronized static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    private CrashHandler() {
    }

    public void initCrashHandler(Context context) {
        Log.d(TAG,"initCrashHandler");
        this.context = context;
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(t, e);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        collectionDeviceInfo(e);
        return true;
    }

    private void collectionDeviceInfo(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        StringBuffer stringBuffer = stringWriter.getBuffer();

        StringBuilder builder = new StringBuilder();
        builder.append(stringBuffer.toString()).append("\n");

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                builder.append(field.getName()).append(":")
                        .append(field.get("").toString()).append("\n");
            } catch (IllegalAccessException ex) {
                Log.e(TAG,"catch crash exception illegalAccess",ex);
            }
        }

        Log.f(TAG, builder.toString());
    }
}
