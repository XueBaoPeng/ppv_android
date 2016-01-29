package com.star.mobile.video.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.star.mobile.video.R;

public class CalculationTimeUtil {
	
	
	public static String getTimeDisplay(Date getDate,Context context) {
		if(getDate==null)
			return "";
      
		final long getTime = getDate.getTime();  
		  
        final long currTime = System.currentTimeMillis();  
        final Date formatSysDate = new Date(currTime);  
  
        // 判断当前总天数  
        final int sysMonth = formatSysDate.getMonth() + 1;  
        final int sysYear = formatSysDate.getYear();  
  
        // 计算服务器返回时间与当前时间差值  
        final long seconds = (currTime - getTime) / 1000;  
        final long minute = seconds / 60;  
        final long hours = minute / 60;  
        final long day = hours / 24;  
        final long month = day / calculationDaysOfMonth(sysYear, sysMonth);  
        final long year = month / 12;  
  
        if (year > 0 || month > 0 || day > 0) {  
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(  
                    "MM/dd/yyyy HH:mm");  
            return simpleDateFormat.format(getDate);  
        } else if (hours > 0) {  
            return hours +" "+ context.getString(R.string.str_hoursago);  
        } else if (minute > 0) {  
            return minute +" "+ context.getString(R.string.str_minsago);  
        } else if (seconds > 0) {  
            return "1" + " "+context.getString(R.string.str_minsago);  
            // return seconds + context.getString(R.string.str_secondago);  
        } else {  
//          return "1" + context.getString(R.string.str_secondago);  
            return "1" + " "+context.getString(R.string.str_minsago); //都换成分钟前  
        }  
	}
	
	/** 
     * 计算月数 
     *  
     * @return 
     */  
    private static int calculationDaysOfMonth(int year, int month) {  
        int day = 0;  
        switch (month) {  
        // 31天  
        case 1:  
        case 3:  
        case 5:  
        case 7:  
        case 8:  
        case 10:  
        case 12:  
            day = 31;  
            break;  
        // 30天  
        case 4:  
        case 6:  
        case 9:  
        case 11:  
            day = 30;  
            break;  
        // 计算2月天数  
        case 2:  
            day = year % 100 == 0 ? year % 400 == 0 ? 29 : 28  
                    : year % 4 == 0 ? 29 : 28;  
            break;  
        }  
  
        return day;  
    }  

}
