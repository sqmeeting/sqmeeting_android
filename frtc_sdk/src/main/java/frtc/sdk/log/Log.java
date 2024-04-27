package frtc.sdk.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class Log {

    private final static String TAG = "Log";
    private static final String LOG_EXT = ".log";
    private static final String ZIP_EXT = ".zip";
    private static final String TIMESTAMP_FORMAT = "MM-dd HH:mm:ss.SSS";
    private static final String LOG_FOLDER_NAME = "frtc";
    private static final String LOG_FILE_NAME = "frtc_android";

    private static File LOG_DIR;
    private static String logFilePath;

    private static final int FILE_COUNT = 5;
    private static final int FILE_SIZE = 51200000;

    private final static boolean logcatEnable = true;
    public final static int FATAL_LEVEL = 7;
    public final static int ERROR_LEVEL = 6;
    public final static int WARNING_LEVEL = 5;
    public final static int INFO_LEVEL = 4;
    public final static int DEBUG_LEVEL = 3;
    public final static int VERBOSE_LEVEL = 2;
    private final static int LOG_LEVEL = VERBOSE_LEVEL;

    private final static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final static ArrayList<String> cacheLogList = new ArrayList<>();
    private File currentLogFile = null;

    private Thread writeLogThread;
    private static boolean initialized = false;

    private static int myPid;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);

    private static Context mContext;
    private static Log instance;

    private Log(){

    }

    public static Log getInstance(){
        if(instance == null){
            instance = new Log();
        }
        return instance;
    }

    public void init(Context context){
        Log.d(TAG,"init:");

        mContext = context;
        LOG_DIR = mContext.getExternalFilesDir("");
        logFilePath = LOG_DIR.getAbsolutePath() + "/" + LOG_FILE_NAME + LOG_EXT;

        myPid = android.os.Process.myPid();
        boolean isDirReady = mkLogFilesDir();

        if(!isDirReady){
            Log.e(TAG,"init failed: make files dir failed.");
        }else{
            initialized = true;
            appendInitialString();
            appendDeviceLog();
            startWriteLogThread();
        }
    }

    public static void v(String tag, String msg) {
        if(LOG_LEVEL <= VERBOSE_LEVEL){
            if(logcatEnable){
                android.util.Log.v(tag,msg);
            }
            if(initialized){
                appendLog("V",tag,msg);
            }

        }
    }

    public static void d(String tag, String msg) {
        if(LOG_LEVEL <= DEBUG_LEVEL){
            if(logcatEnable){
                android.util.Log.d(tag,msg);
            }
            if(initialized){
                appendLog("D",tag,msg);
            }
        }
    }

    public static void i(String tag, String msg) {
        if(LOG_LEVEL <= INFO_LEVEL){
            if(logcatEnable){
                android.util.Log.i(tag,msg);
            }
            if(initialized){
                appendLog("I",tag,msg);
            }

        }
    }

    public static void w(String tag, String msg) {
        if(LOG_LEVEL <= WARNING_LEVEL){
            if(logcatEnable){
                android.util.Log.w(tag,msg);
            }
            if(initialized){
                appendLog("W",tag,msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if(LOG_LEVEL <= ERROR_LEVEL){
            if(logcatEnable){
                android.util.Log.e(tag,msg);
            }
            if(initialized){
                appendLog("E",tag,msg);
            }
        }
    }

    public static void f(String tag, String msg) {
        if(LOG_LEVEL <= FATAL_LEVEL){
            if(logcatEnable){
                android.util.Log.e(tag,msg);
            }
            if(initialized){
                appendDeviceLog();
                appendLog("F",tag,msg);
            }
        }
    }

    public static void e(String tag, String msg, Throwable ex){
        if(ex == null){
            e(tag,msg);
        }else{
            StringWriter stringWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(stringWriter));
            e(tag, msg + "\n" + stringWriter.toString());
        }
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[6];
    }

    private static synchronized void appendLog(String priority, String tag, String msg){
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date())).append(" ")
                .append(myPid).append(" ")
                .append(android.os.Process.myTid()).append(" ")
                .append(priority).append(" ")
                .append("[")
                .append(getCallerStackTraceElement().getFileName())
                .append(":").append(getCallerStackTraceElement().getLineNumber())
                .append("]").append(" ")
                .append(tag).append(":")
                .append(msg).append("\n");
        pushLog(builder.toString());
    }


    private static void appendDeviceLog(){
        StringBuilder builder = new StringBuilder();
        builder.append(dateFormat.format(new Date())).append(" ")
                .append(myPid).append(" ")
                .append(android.os.Process.myTid())
                .append("I").append(" ")
                .append("[")
                .append(getCallerStackTraceElement().getFileName())
                .append(":").append(getCallerStackTraceElement().getLineNumber())
                .append("]").append(" ")
                .append(TAG).append(": ")
                .append(logDeviceInfo()).append("\n");
        pushLog(builder.toString());
    }

    private void appendInitialString(){
        pushLog(getInitialStr());
    }

    private static void pushLog(String message){
        queue.offer(message);
    }

    private void pollLog(){
        String message = queue.poll();
    }

    private void startWriteLogThread(){
        Log.d(TAG,"startWriteLogThread:");
        writeLogThread = new Thread(new writeLogLoop(),"write-log-thread");
        writeLogThread.start();
    }

    private void stopWriteLogThread(){
        Log.d(TAG,"stopWriteLogThread:");
        try{
            initialized = false;
            writeLogThread.join();
            writeLogThread = null;
            queue.clear();
            cacheLogList.clear();
        }catch (InterruptedException exception){
            Log.e(TAG,"stopWriteLogThread failed:"+exception.toString());
        }
    }

    private class writeLogLoop implements Runnable{
        @Override
        public void run() {
            while(initialized){

                currentLogFile = getCurrentLogFile();

                if(!currentLogFile.exists()){
                    try {
                        currentLogFile.createNewFile();
                    } catch (IOException e) {
                        Log.e(TAG,"create log file failed:"+e);
                    }
                }

                if(queue.isEmpty() && cacheLogList.isEmpty()){
                    try{
                        Thread.sleep(10);
                    }catch (InterruptedException e){
                        Log.e(TAG,"writeLogLoop can not sleep:"+e);
                    }
                }

                int remainingSize = (int) (FILE_SIZE - currentLogFile.length());
                if (!queue.isEmpty()) {
                    do {
                        String item = queue.poll();
                        if (item != null) {
                            cacheLogList.add(item);
                            remainingSize -= item.length();
                        }
                    } while (queue.size() > 0 && remainingSize > 0);
                }

                if(!cacheLogList.isEmpty()){
                    try {
                        Iterator<String> iterator = cacheLogList.iterator();
                        FileOutputStream fos = new FileOutputStream(currentLogFile,true);
                        OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                        BufferedWriter out = new BufferedWriter(writer);
                        while(iterator.hasNext()){
                            String message = iterator.next();
                            out.write(message);
                        }
                        cacheLogList.clear();
                        out.flush();
                        out.close();
                        writer.close();
                        fos.close();
                    } catch (IOException exception) {
                        Log.e(TAG,"write log to file failed"+exception);
                    }
                }
            }
        }
    }

    private File getCurrentLogFile(){

        if(currentLogFile == null){
            currentLogFile = getLogFile();
        }

        if(currentLogFile.length() >= FILE_SIZE){
            return getNextLogFile(currentLogFile);
        }

        return currentLogFile;

    }

    private int getLogFilesNum(){
        int num = 0;
        if (LOG_DIR.exists() && LOG_DIR.isDirectory()) {
            File[] files = LOG_DIR.listFiles();
            if(files == null || files.length == 0){
                return 0;
            }else{
                for (File f : files) {
                    if(f.isFile() && f.getName().contains(LOG_FILE_NAME)){
                        num++;
                    }
                }
            }
        }
        return num;
    }

    private File getNextLogFile(File latestFile){
        File nextFile = null;
        String filePath = "";

        String path = latestFile.getPath();
        if(logFilePath.equals(path)){
            filePath = logFilePath+"."+1;
        }else{
            int suffix = Integer.parseInt(path.substring(path.lastIndexOf(".")+1));
            if(suffix == 4){
                filePath = logFilePath;
            }else{
                filePath = logFilePath+(suffix+1);
            }
        }
        nextFile = new File(filePath);
        clearLogFile(nextFile);

        Log.d(TAG,"getNextLogFile:"+nextFile.getName());
        return nextFile;
    }

    private File getLogFile(){
        File nextFile = null;
        if (LOG_DIR.exists() && LOG_DIR.isDirectory()) {
            File[] allFiles = LOG_DIR.listFiles();

            if(allFiles == null || allFiles.length == 0){
                nextFile = new File(logFilePath);
            }else{
                ArrayList<File> files = new ArrayList<>();

                for (File f : allFiles) {
                    if(f.isFile() && f.getName().contains(LOG_FILE_NAME)){
                        files.add(f);
                    }
                }

                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return Long.compare(o2.lastModified(), o1.lastModified());
                    }
                });

                if(!files.isEmpty()){
                    File latestFile = files.get(0);
                    if(latestFile.length() < FILE_SIZE){
                        nextFile = latestFile;
                    }else{
                        String filePath = "";
                        String path = latestFile.getPath();
                        if(logFilePath.equals(path)){
                            filePath = logFilePath+"."+1;
                        }else{
                            int suffix = Integer.parseInt(path.substring(path.lastIndexOf(".")+1));
                            if(suffix == 4){
                                filePath = logFilePath;
                            }else{
                                filePath = logFilePath+(suffix+1);
                            }
                        }
                        nextFile = new File(filePath);
                        clearLogFile(nextFile);
                    }
                }else{
                    nextFile = new File(logFilePath);
                }
            }
        }
        Log.d(TAG,"getLogFile:"+nextFile.getName());
        return nextFile;
    }

    private void clearLogFile(File file){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Log.e(TAG,"clear log file "+file.getName()+" failed:"+e);
        }
    }

    private boolean mkLogFilesDir() {

        if (!LOG_DIR.exists()) {
            boolean mkdirs = LOG_DIR.mkdirs();
            if (!mkdirs) {
                Log.e(TAG, "create log dir failed");
                return false;
            } else {
                Log.i(TAG, "create log dir successfully");
            }
        } else {
            Log.i(TAG, "logDir = " + LOG_DIR);
        }
        return true;
    }

    private String getInitialStr(){
        String str = "--------- beginning of main ---------"+"\n";
        return str;
    }


    private static String logDeviceInfo() {
        String info = "Model: " + android.os.Build.MODEL +
                " Brand : " + android.os.Build.BRAND +
                " Product: " + android.os.Build.PRODUCT +
                " Device: " + android.os.Build.DEVICE +
                " Codename: " + android.os.Build.VERSION.CODENAME +
                " Release: " + android.os.Build.VERSION.RELEASE;

        try{
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if(pi != null){
                info = info + " versionName: " + pi.versionName
                        + " versionCode: " + pi.versionCode;
            }
        }catch (Exception e){
            Log.e(TAG,"getPackageInfo failed");
        }

        return info;
    }

}
