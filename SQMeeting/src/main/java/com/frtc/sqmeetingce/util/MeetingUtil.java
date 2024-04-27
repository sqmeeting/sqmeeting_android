package com.frtc.sqmeetingce.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.frtc.sqmeetingce.R;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import frtc.sdk.log.Log;
import frtc.sdk.ui.model.RecurrenceType;

public class MeetingUtil {

    protected static final String TAG = MeetingUtil.class.getSimpleName();

    private static final String HARMONY_OS = "harmony";

    public static final String URL_KEY = "url";

    public static boolean isRunningBackground(Context context) {
        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = actManager.getRunningAppProcesses();
        if(runningAppProcesses == null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.processName.equals(context.getPackageName())) {
                return ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND != runningAppProcessInfo.importance;
            }
        }
        return false;
    }

    public static String getRandom(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(10));
        }
        return rs.toString();
    }

    public static boolean isHarmonyOS() {
        try {
            Class clz = Class.forName("com.huawei.system.BuildEx");
            Method method = clz.getMethod("getOsBrand");
            return HARMONY_OS.equals(method.invoke(clz));
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "occured ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "occured NoSuchMethodException");
        } catch (Exception e) {
            Log.i(TAG, "occured Exception" + e.getLocalizedMessage());
        }
        return false;
    }

    public static String dateToWeek(Context context, String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { context.getResources().getString(R.string.sunday), context.getResources().getString(R.string.monday),
                context.getResources().getString(R.string.tuesday), context.getResources().getString(R.string.wednesday),
                context.getResources().getString(R.string.thursday), context.getResources().getString(R.string.friday),
                context.getResources().getString(R.string.saturday)};
        Calendar cal = Calendar.getInstance();
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


    public static int dateToWeekPosition(Context context, String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { context.getResources().getString(R.string.sunday), context.getResources().getString(R.string.monday),
                context.getResources().getString(R.string.tuesday), context.getResources().getString(R.string.wednesday),
                context.getResources().getString(R.string.thursday), context.getResources().getString(R.string.friday),
                context.getResources().getString(R.string.saturday)};
        Calendar cal = Calendar.getInstance();
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    public static String timeFormat(long time, String strFormat){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
        Date date = new Date(time);
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public static String strTimeFormat(String timeStr, String strFormat){
        if(timeStr != null && !timeStr.isEmpty()){
            long timeMill = Long.parseLong(timeStr);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormat);
            Date date = new Date(timeMill);
            return simpleDateFormat.format(date);
        }
        return "";
    }

    public static long timeFormatToLong(String timeFormat, String strFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        Date date;
        try {
            date = sdf.parse(timeFormat);
        } catch (ParseException e) {
            Log.e(TAG,"ParseException:"+e.toString());
            return 0;
        }
        return date.getTime();
    }

    public static long calcDurationTime(Context context, String start, String end){

        if(start != null && !start.isEmpty() && end != null &&
                !end.isEmpty()){
            long startTime = Long.parseLong(start);
            long endTime = Long.parseLong(end);
            long durationTime = endTime - startTime;
            return durationTime;
        }
        return 0;
    }

    public static String formatDurationTime(Context context, long durationTime){

        if(durationTime != 0){
            long durationSeconds = durationTime/1000;
            long hour = durationSeconds / 3600;
            long seconds = durationSeconds % 60;
            long minutes = durationSeconds % 3600 / 60;
            if(hour > 0){
                if(minutes == 0){
                    return String.valueOf(hour) + context.getString(R.string.time_hours);
                }
                return String.valueOf(hour) + context.getString(R.string.time_hours) + String.valueOf(minutes) + context.getString(R.string.time_minutes) ;
            }else{
                return String.valueOf(minutes) + context.getString(R.string.time_minutes) ;
            }
        }
        return "";
    }

    public static String formatRecurrenceContent(Context context, String recurrenceType, int recurrenceInterval,
                                                 List<Integer> recurrenceDaysOfWeek, List<Integer> recurrenceDaysOfMonth) {
        String str = "";
        String str1 = context.getString(frtc.sdk.R.string.repetition_frequency_content);
        if(recurrenceType.equals(RecurrenceType.DAILY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(frtc.sdk.R.string.repetition_type_one_day));
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(frtc.sdk.R.string.repetition_type_day));
            }
        }else if(recurrenceType.equals(RecurrenceType.WEEKLY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(frtc.sdk.R.string.repetition_type_one_week)) + "(";
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(frtc.sdk.R.string.repetition_type_week)) + "(";
            }
            for(int i = 0; i < recurrenceDaysOfWeek.size(); i++){
                if(i == 0){
                    str += formatWeekDay(context, recurrenceDaysOfWeek.get(i));
                }else {
                    str += "、" + formatWeekDay(context, recurrenceDaysOfWeek.get(i));
                    if(i == recurrenceDaysOfWeek.size()-1){
                        str += ")";
                    }
                }
            }
        }else if(recurrenceType.equals(RecurrenceType.MONTHLY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(frtc.sdk.R.string.repetition_type_one_month)) + "(";
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(frtc.sdk.R.string.repetition_type_month)) + "(";
            }
            for(int i = 0; i < recurrenceDaysOfMonth.size(); i++){
                if(i == 0){
                    str += recurrenceDaysOfMonth.get(i) + "";
                    if(recurrenceDaysOfMonth.size() == 1){
                        str += context.getResources().getString(frtc.sdk.R.string.repetition_type_day_th) + ")";
                    }
                }else {
                    str += "、" + recurrenceDaysOfMonth.get(i) + "";
                    if(i == recurrenceDaysOfMonth.size()-1){
                        str += context.getResources().getString(frtc.sdk.R.string.repetition_type_day_th) + ")";
                    }
                }
            }
        }
        return str;
    }

    public static String formatRecurrenceTypeContent(Context context, String recurrenceType, int recurrenceInterval) {
        String str = "";
        String str1 = context.getString(frtc.sdk.R.string.repetition_frequency_content);
        if(TextUtils.isEmpty(recurrenceType)){
            return str;
        }
        if(recurrenceType.equals(RecurrenceType.DAILY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(frtc.sdk.R.string.repetition_type_one_day));
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(frtc.sdk.R.string.repetition_type_day));
            }
        }else if(recurrenceType.equals(RecurrenceType.WEEKLY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(frtc.sdk.R.string.repetition_type_one_week));
            } else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(frtc.sdk.R.string.repetition_type_week)) ;
            }
        }else if(recurrenceType.equals(RecurrenceType.MONTHLY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(frtc.sdk.R.string.repetition_type_one_month));
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(frtc.sdk.R.string.repetition_type_month));
            }
        }
        return str;
    }

    private static String formatWeekDay(Context context, int day){
        switch (day){
            case 1:
                return context.getResources().getString(frtc.sdk.R.string.sunday);
            case 2:
                return context.getResources().getString(frtc.sdk.R.string.monday);
            case 3:
                return context.getResources().getString(frtc.sdk.R.string.tuesday);
            case 4:
                return context.getResources().getString(frtc.sdk.R.string.wednesday);
            case 5:
                return context.getResources().getString(frtc.sdk.R.string.thursday);
            case 6:
                return context.getResources().getString(frtc.sdk.R.string.friday);
            case 7:
                return context.getResources().getString(frtc.sdk.R.string.saturday);
            default:
                return context.getResources().getString(frtc.sdk.R.string.sunday);
        }
    }

    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        Instant instant = date.toInstant();
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        return LocalDateTime.ofInstant(instant, zoneId);
    }


    public static Date LocalDateTimetoDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    public static boolean isInvalidDate(int day, Calendar nextExecutionTime) {
        if (day > 28) {
            nextExecutionTime.setLenient(false);
            try {
                nextExecutionTime.getTime();
            } catch (IllegalArgumentException e) {
                return true;
            }
        }
        return false;
    }
}
