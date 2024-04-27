package com.frtc.sqmeetingce.ui.calendar;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import java.util.TimeZone;


public class CalendarProviderManager {

    private static StringBuilder builder = new StringBuilder();
    private static String CALENDAR_NAME = "我的日历表";
    private static String CALENDAR_ACCOUNT_NAME = "myAccount";
    private static String CALENDAR_DISPLAY_NAME = "myDisplayName";
    @SuppressWarnings("WeakerAccess")
    public static long obtainCalendarAccountID(Context context) {
        long calID = checkCalendarAccount(context);
        if (calID >= 0) {
            return calID;
        } else {
            return createCalendarAccount(context);
        }
    }

    private static long checkCalendarAccount(Context context) {
        try (Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                null, null, null, null)) {
            if (null == cursor) {
                return -1;
            }
            int count = cursor.getCount();
            if (count > 0) {
                cursor.moveToFirst();
                return cursor.getInt(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        }
    }

    private static long createCalendarAccount(Context context) {

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Uri accountUri;

        ContentValues account = new ContentValues();
        account.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        account.put(CalendarContract.Calendars.NAME, CALENDAR_NAME);
        account.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME);
        account.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_DISPLAY_NAME);
        account.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor("#515bd4"));
        account.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        account.put(CalendarContract.Calendars.VISIBLE, 1);
        account.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
        account.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1);
        account.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        account.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDAR_ACCOUNT_NAME);
        account.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1);
        account.put(CalendarContract.Calendars.MAX_REMINDERS, 8);
        account.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "0,1,2,3,4");
        account.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "0,1,2");

        uri = uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.Calendars.CALENDAR_LOCATION)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                    "android.permission.WRITE_CALENDAR")) {
                accountUri = context.getContentResolver().insert(uri, account);
            } else {
                return -2;
            }
        } else {
            accountUri = context.getContentResolver().insert(uri, account);
        }
        return accountUri == null ? -1 : ContentUris.parseId(accountUri);
    }


    public static int addCalendarEvent(Context context, CalendarEvent calendarEvent) {

        long calID = obtainCalendarAccountID(context);
        Uri uri1 = CalendarContract.Events.CONTENT_URI;
        Uri eventUri;

        Uri uri2 = CalendarContract.Reminders.CONTENT_URI;
        Uri reminderUri;

        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, calID);
        setupEvent(calendarEvent, event);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(
                    "android.permission.WRITE_CALENDAR")) {
                eventUri = context.getContentResolver().insert(uri1, event);
            } else {
                return -2;
            }
        } else {
            eventUri = context.getContentResolver().insert(uri1, event);
        }

        if (null == eventUri) {
            return -1;
        }


        if (-2 != calendarEvent.getAdvanceTime()) {
            long eventID = ContentUris.parseId(eventUri);
            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminders.put(CalendarContract.Reminders.MINUTES, calendarEvent.getAdvanceTime());
            reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            reminderUri = context.getContentResolver().insert(uri2, reminders);

            if (null == reminderUri) {
                return -1;
            }
        }

        return 0;
    }


    private static void setupEvent(CalendarEvent calendarEvent, ContentValues event) {
        event.put(CalendarContract.Events.DTSTART, calendarEvent.getStart());
        event.put(CalendarContract.Events.DTEND, calendarEvent.getEnd());
        event.put(CalendarContract.Events.TITLE, calendarEvent.getTitle());
        event.put(CalendarContract.Events.DESCRIPTION, calendarEvent.getDescription());
        event.put(CalendarContract.Events.EVENT_LOCATION, calendarEvent.getEventLocation());
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        event.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
        event.put(CalendarContract.Events.STATUS, 0);
        event.put(CalendarContract.Events.HAS_ALARM, 1);
        event.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        if (null != calendarEvent.getRRule()) {
            event.put(CalendarContract.Events.RRULE, "");
        }
    }


    public static String getCalendarName() {
        return CALENDAR_NAME;
    }

    public static void setCalendarName(String calendarName) {
        CALENDAR_NAME = calendarName;
    }

    public static String getCalendarAccountName() {
        return CALENDAR_ACCOUNT_NAME;
    }

    public static void setCalendarAccountName(String calendarAccountName) {
        CALENDAR_ACCOUNT_NAME = calendarAccountName;
    }

    public static String getCalendarDisplayName() {
        return CALENDAR_DISPLAY_NAME;
    }

    public static void setCalendarDisplayName(String calendarDisplayName) {
        CALENDAR_DISPLAY_NAME = calendarDisplayName;
    }

}
