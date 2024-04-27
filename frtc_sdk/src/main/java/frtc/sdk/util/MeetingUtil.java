package frtc.sdk.util;

import android.content.Context;

import java.util.List;

import frtc.sdk.R;
import frtc.sdk.ui.model.RecurrenceType;

public class MeetingUtil {

    public static String formatRecurrenceContent(Context context, String recurrenceType, int recurrenceInterval, 
                                                 List<Integer> recurrenceDaysOfWeek, List<Integer> recurrenceDaysOfMonth) {
        String str = "";
        String str1 = context.getString(R.string.repetition_frequency_content);
        if(recurrenceType.equals(RecurrenceType.DAILY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(R.string.repetition_type_one_day));
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(R.string.repetition_type_day));
            }
        }else if(recurrenceType.equals(RecurrenceType.WEEKLY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(R.string.repetition_type_one_week)) + "(";
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(R.string.repetition_type_week)) + "(";
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
                str = String.format(str1, context.getResources().getString(R.string.repetition_type_one_month)) + "(";
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(R.string.repetition_type_month)) + "(";
            }
            for(int i = 0; i < recurrenceDaysOfMonth.size(); i++){
                if(i == 0){
                    str += recurrenceDaysOfMonth.get(i) + "";
                    if(recurrenceDaysOfMonth.size() == 1){
                        str += context.getResources().getString(R.string.repetition_type_day_th) + ")";
                    }
                }else {
                    str += "、" + recurrenceDaysOfMonth.get(i) + "";
                    if(i == recurrenceDaysOfMonth.size()-1){
                        str += context.getResources().getString(R.string.repetition_type_day_th) + ")";
                    }
                }
            }
        }
        return str;
    }

    public static String formatRecurrenceTypeContent(Context context, String recurrenceType, int recurrenceInterval) {
        String str = "";
        String str1 = context.getString(R.string.repetition_frequency_content);
        if(recurrenceType.equals(RecurrenceType.DAILY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(R.string.repetition_type_one_day));
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(R.string.repetition_type_day));
            }
        }else if(recurrenceType.equals(RecurrenceType.WEEKLY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(R.string.repetition_type_one_week));
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(R.string.repetition_type_week)) ;
            }
        }else if(recurrenceType.equals(RecurrenceType.MONTHLY.getTypeName())){
            if(recurrenceInterval == 1){
                str = String.format(str1, context.getResources().getString(R.string.repetition_type_one_month));
            }else {
                str = String.format(str1, recurrenceInterval + context.getResources().getString(R.string.repetition_type_month));
            }
        }
        return str;
    }

    private static String formatWeekDay(Context context, int day){
        switch (day){
            case 1:
                return context.getResources().getString(R.string.sunday);
            case 2:
                return context.getResources().getString(R.string.monday);
            case 3:
                return context.getResources().getString(R.string.tuesday);
            case 4:
                return context.getResources().getString(R.string.wednesday);
            case 5:
                return context.getResources().getString(R.string.thursday);
            case 6:
                return context.getResources().getString(R.string.friday);
            case 7:
                return context.getResources().getString(R.string.saturday);
            default:
                return context.getResources().getString(R.string.sunday);
        }
    }
}
