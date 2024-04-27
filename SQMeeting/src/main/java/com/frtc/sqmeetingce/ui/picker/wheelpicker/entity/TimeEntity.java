/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.frtc.sqmeetingce.ui.picker.wheelpicker.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间数据实体
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2019/6/17 15:29
 */
@SuppressWarnings({"unused"})
public class TimeEntity implements Serializable {
    private int hour;
    private int minute;
    private int second;
    private static long mStartTimeMill;

    public static TimeEntity target(int hourOfDay, int minute, int second) {
        TimeEntity entity = new TimeEntity();
        entity.setHour(hourOfDay);
        entity.setMinute(minute);
        entity.setSecond(second);
        return entity;
    }

    public static TimeEntity target(Calendar calendar) {
        if(mStartTimeMill > System.currentTimeMillis()){
            calendar.setTimeInMillis(mStartTimeMill);
        }else{
            calendar.setTimeInMillis(System.currentTimeMillis());
            int minute = calendar.get(android.icu.util.Calendar.MINUTE);
            if(minute <30){
                calendar.set(android.icu.util.Calendar.MINUTE,30);
            }else{
                calendar.set(android.icu.util.Calendar.MINUTE,0);
                calendar.add(android.icu.util.Calendar.HOUR_OF_DAY, 1);
            }
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE)/30;
        int second = calendar.get(Calendar.SECOND);
        return target(hour, minute, second);
    }

    public static TimeEntity target(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return target(calendar);
    }

    public static TimeEntity now() {
        return target(Calendar.getInstance());
    }

    public static TimeEntity minuteOnFuture(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return target(calendar);
    }

    public static TimeEntity hourOnFuture(int hourOfDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hourOfDay);
        return target(calendar);
    }

    public static void setStartTimeMill(long startTimeMill) {
        mStartTimeMill = startTimeMill;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public long toTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @NonNull
    @Override
    public String toString() {
        return hour + ":" + minute + ":" + second;
    }

}
