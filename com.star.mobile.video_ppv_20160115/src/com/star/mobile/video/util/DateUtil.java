package com.star.mobile.video.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {
	/**
	 * 获取本月第一天时间
	 * @param format
	 * @return
	 */
	public static Date getFirstDay(Date date) {
		Calendar cal = Calendar.getInstance();   
		cal.setTime(date);
	    cal.set(GregorianCalendar.DAY_OF_MONTH, 1);   
	    return cal.getTime();  
	}
	
	/**
	 * 获取本月做后一天时间
	 * @param format
	 * @return
	 */
	public static Date getLastDay(Date date) {
		Calendar cal = Calendar.getInstance();   
		cal.setTime(date);
	    cal.set( Calendar.DATE, 1 );  
	    cal.roll(Calendar.DATE, - 1 );  
	    return cal.getTime();
	}
	
	
	/**
	 * 获取当月前几个月
	 * @param month 月数
	 * @param format
	 * @return
	 */
	public static List<String> getMonths(int month,String format) {
		SimpleDateFormat datef = new SimpleDateFormat(format);
		List<String> months = new ArrayList<String>();
		Date date = new Date();
		Calendar calendar= Calendar.getInstance();
		for(int i = 0;i < month;i++) {
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, -i);
			months.add(datef.format(calendar.getTime()));
		}
			
		return months;
	}

	/**
	 * 计算两个时间相差的天数
	 * @param currentDate
	 * @param endDate
	 * @return
	 */
	public static int sunDays(Date currentDate,Date endDate) {
		Long times = currentDate.getTime() - endDate.getTime();
		if(times < 0) {
			times = endDate.getTime() - currentDate.getTime();
		}
		if(times < 0) {
			return 0;
		} else {
			int day = 1000 * 60 * 60 * 24;
			return (int)(times / day);
		}
	}
	
}
