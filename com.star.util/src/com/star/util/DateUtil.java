package com.star.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;



public class DateUtil {
	public final static long anHour = 3600L * 1000L;
	private final static long anMinute = 60L * 1000L;
	private final static long anSecond = 1000L;
	
    private static Date date = null;
    private static Date begindate = null;
    public static Date convertTimestampToDate(int timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Long time = (long) timestamp;
        String d = format.format(time * 1000);
        Date date = null;
        try {
            date = format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    public static String convertTimestampToMinu(long timestamp) {
    	int minutes = (int)(timestamp/anMinute);
    	int seconds = (int)(timestamp/anSecond)%60;
    	String second;
    	if((seconds > 0 || seconds == 0) && (seconds < 9)) {
    		second  = "0" + seconds;
    	} else {
    		second = "" + seconds;
    	}
    	String minute;
    	if((minutes > 0 || minutes == 0) && (minutes < 9)) {
    		minute  = "0" + minutes;
    	} else {
    		minute = "" + minutes;
    	}
    	
    	String d = minute + ":" + second;
    	return d;
    }
    
    public static String convertTimestampToString(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Long time = (long) timestamp;
        Date date = new Date();
        date.setTime(time);
        String d = format.format(date);
        return d;
    }    

    public static int convertDateToTimestamp(Date date) {
        return (int) (date.getTime() / 1000);
    }

    public static boolean isTimeBetween(int mid, int before, int after) {
        if (before > after) {
            int temp = before;
            before = after;
            after = temp;
        }
        if (mid >= before && mid <= after) {
            return true;
        }
        return false;
    }

    public static Date getBeginDate() {
        if (begindate == null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            try {
                begindate = df.parse("2013-08-15 12:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return begindate;
    }
    
    
    public static Date getNowDate() {
        if (date == null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            try {
                date = df.parse("2013-08-15 12:11");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static void setNowDate(Date newDate) {
        date = newDate;
    }

    public static int getNowTimestamp() {
//        if (date != null) {
//            int now = (int) (date.getTime() / 1000);
//            return now;
//        }
        return 1376539860;
    }

    public static String data2Str(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String string = df.format(date);
        return string;
    }

    public static Calendar getNearCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getNowDate());
        int minute = calendar.get(Calendar.MINUTE);
        if (minute > 30) {
            minute = 30;
        } else {
            minute = 0;
        }
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    public static boolean date1BeforeDate2(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        return cal1.before(cal2) || cal1.equals(cal2);
    }

    public static Date str2Date(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            Date date = formatter.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            //return new Date();
        }
    }
    
    public static int minDistance(Date date1 , Date date2){
        long long1 = date1.getTime();
        long long2 = date2.getTime();
        if(long1 == long2) return 0;
        long dis = 0;
        if(long1 > long2){
            dis = long1 - long2;
        } else {
            dis = long2 - long1;
        }
        //TODO 进位？
        int secondDis = (int) (dis/1000);
        return secondDis/60;
    }
    public static Date getNow(){
        //return DateUtil.str2Date("2014-01-14 08:10:00");
    	return new Date();
    }
    public static String getTodayString(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR); 
        int month = cal.get(Calendar.MONTH); 
        int day=cal.get(Calendar.DATE); 
        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK); 
        return " TODAY" + " " + new Date().toString().substring(4,7)+"."+day;
    }
    
    public static int subDate(Date a,Date b,TimeZone tz){
    	return (int)( (a.getTime() + tz.getRawOffset()) / (24 *anHour) - (b.getTime() + tz.getRawOffset()) / (24 * anHour));
    }
    
    public static String getMonthStr(Calendar c,Context context){
    	switch(c.get(Calendar.MONTH)){
    	case 0:
    		return context.getResources().getString(R.string.month_1);
    	case 1:
    		return context.getResources().getString(R.string.month_2);
    	case 2:
    		return context.getResources().getString(R.string.month_3);
    	case 3:
    		return context.getResources().getString(R.string.month_4);
    	case 4:
    		return context.getResources().getString(R.string.month_5);
    	case 5:
    		return context.getResources().getString(R.string.month_6);
    	case 6:
    		return context.getResources().getString(R.string.month_7);
    	case 7:
    		return context.getResources().getString(R.string.month_8);
    	case 8:
    		return context.getResources().getString(R.string.month_9);
    	case 9:
    		return context.getResources().getString(R.string.month_10);
    	case 10:
    		return context.getResources().getString(R.string.month_11);
    	case 11:
    		return context.getResources().getString(R.string.month_12);
    	}
    	return "";
    }
    
    public static String getWeekStr(Calendar c, Context context){
    	switch(c.get(Calendar.DAY_OF_WEEK)){
    	case Calendar.MONDAY:
    		return context.getResources().getString(R.string.week_1);
    	case Calendar.TUESDAY:	
    		return context.getResources().getString(R.string.week_2);
    	case Calendar.WEDNESDAY:
    		return context.getResources().getString(R.string.week_3);
    	case Calendar.THURSDAY:
    		return context.getResources().getString(R.string.week_4);
    	case Calendar.FRIDAY:
    		return context.getResources().getString(R.string.week_5);
    	case Calendar.SATURDAY:
    		return context.getResources().getString(R.string.week_6);
    	case Calendar.SUNDAY:
    		return context.getResources().getString(R.string.week_7);
    	}
    	return "";
    }
    
    public static String getDayString(Calendar c,Date now ,Context context){
    	Locale locale = context.getResources().getConfiguration().locale;
    	String format = "%s\t%2d %3s %4s"; 
    	if(subDate(c.getTime(), now , c.getTimeZone()) == 0){
    		//如果是英文
    		return String.format(format, "TODAY",c.get(Calendar.DAY_OF_MONTH),DateUtil.getMonthStr(c, context),c.get(Calendar.YEAR));
    	}
    	return String.format(format, getWeekStr(c,context),c.get(Calendar.DAY_OF_MONTH),DateUtil.getMonthStr(c, context),c.get(Calendar.YEAR));
    }
    
    

}
