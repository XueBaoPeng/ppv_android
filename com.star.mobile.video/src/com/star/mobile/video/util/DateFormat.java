package com.star.mobile.video.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.star.cms.model.Area;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class DateFormat {
	
	public static String format(Date date,String timeFormate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormate);
		return simpleDateFormat.format(date);
	}
	
	public static String format(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		return simpleDateFormat.format(date);
	}
	
	public static String formatTuesday(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd EEEE", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	
	
	public static String formatTime(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		return simpleDateFormat.format(date);
	}
	
	public static String formatMonth(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM.dd",Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	/**
	 * 年月日的格式化，月份为英文的缩写
	 * @param date
	 * @return
	 */
	public static String formatDayAndMonth(Date date){
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-EEEE-MM", Locale.ENGLISH);
		String format=simpleDateFormat.format(date);
		String[] strings=format.split("-");
		if ("01".equals(strings[2])) {
			strings[2] = "Jan.";
		} else if ("02".equals(strings[2])) {
			strings[2] = "Feb.";
		}else if ("03".equals(strings[2])) {
			strings[2] = "Mar.";
		}else if ("04".equals(strings[2])) {
			strings[2] = "Apr.";
		}else if ("05".equals(strings[2])) {
			strings[2] = "May.";
		}else if ("06".equals(strings[2])) {
			strings[2] = "Jun.";
		}else if ("07".equals(strings[2])) {
			strings[2] = "Jul.";
		}else if ("08".equals(strings[2])) {
			strings[2] = "Aug.";
		}else if ("09".equals(strings[2])) {
			strings[2] = "Sep.";
		} else if ("10".equals(strings[2])) {
			strings[2] = "Oct.";
		} else if ("11".equals(strings[2])) {
			strings[2] = "Nov.";
		} else if ("12".equals(strings[2])) {
			strings[2] = "Dec.";
		}
		return strings[0]+"-"+strings[1]+"-"+strings[2];
		
	}
	/**
	 * 年月日的格式化，月份为英文的缩写
	 * @return 
	 * @author Lee
	 */
	public static String formatDateMonthAbbr(Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String format = simpleDateFormat.format(date);
		String[] strings = format.split("/");
		if ("01".equals(strings[1])) {
			strings[1] = "Jan.";
		} else if ("02".equals(strings[1])) {
			strings[1] = "Feb.";
		}else if ("03".equals(strings[1])) {
			strings[1] = "Mar.";
		}else if ("04".equals(strings[1])) {
			strings[1] = "Apr.";
		}else if ("05".equals(strings[1])) {
			strings[1] = "May.";
		}else if ("06".equals(strings[1])) {
			strings[1] = "Jun.";
		}else if ("07".equals(strings[1])) {
			strings[1] = "Jul.";
		}else if ("08".equals(strings[1])) {
			strings[1] = "Aug.";
		}else if ("09".equals(strings[1])) {
			strings[1] = "Sep.";
		} else if ("10".equals(strings[1])) {
			strings[1] = "Oct.";
		} else if ("11".equals(strings[1])) {
			strings[1] = "Nov.";
		} else if ("12".equals(strings[1])) {
			strings[1] = "Dec.";
		}
		return strings[0]+"/"+strings[1]+"/"+strings[2];
	}
	
	/**
	 * 年月日的格式化，月份为英文的缩写
	 * @return 
	 * @author Lee
	 */
	public static String formatDateMonth(Date date,String farmt){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(farmt);
		String format = simpleDateFormat.format(date);
		String[] strings = format.split("-");
		if ("01".equals(strings[1])) {
			strings[1] = "Jan.";
		} else if ("02".equals(strings[1])) {
			strings[1] = "Feb.";
		}else if ("03".equals(strings[1])) {
			strings[1] = "Mar.";
		}else if ("04".equals(strings[1])) {
			strings[1] = "Apr.";
		}else if ("05".equals(strings[1])) {
			strings[1] = "May.";
		}else if ("06".equals(strings[1])) {
			strings[1] = "Jun.";
		}else if ("07".equals(strings[1])) {
			strings[1] = "Jul.";
		}else if ("08".equals(strings[1])) {
			strings[1] = "Aug.";
		}else if ("09".equals(strings[1])) {
			strings[1] = "Sep.";
		} else if ("10".equals(strings[1])) {
			strings[1] = "Oct.";
		} else if ("11".equals(strings[1])) {
			strings[1] = "Nov.";
		} else if ("12".equals(strings[1])) {
			strings[1] = "Dec.";
		}
		String result = "";
		for(int i=0;i<strings.length;i++) {
			if(i != 0) {
				result+="-";
			} 
			result+=strings[i];
		}
		return result;
	}
	
	/**
	 * 年月日的格式化，月份为英文的缩写 
	 * @return 
	 * @author Lee
	 */
	public static String formatDateMonthAbbr(Date date,String fmart){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmart);
		String format = simpleDateFormat.format(date);
		String []hr = format.split(",");
		String[] strings = hr[1].split("-");
		if ("01".equals(strings[0])) {
			strings[1] = "Jan.";
		} else if ("02".equals(strings[0])) {
			strings[0] = "Feb.";
		}else if ("03".equals(strings[0])) {
			strings[0] = "Mar.";
		}else if ("04".equals(strings[0])) {
			strings[0] = "Apr.";
		}else if ("05".equals(strings[0])) {
			strings[0] = "May.";
		}else if ("06".equals(strings[0])) {
			strings[0] = "Jun.";
		}else if ("07".equals(strings[0])) {
			strings[0] = "Jul.";
		}else if ("08".equals(strings[0])) {
			strings[0] = "Aug.";
		}else if ("09".equals(strings[0])) {
			strings[0] = "Sep.";
		} else if ("10".equals(strings[0])) {
			strings[0] = "Oct.";
		} else if ("11".equals(strings[0])) {
			strings[0] = "Nov.";
		} else if ("12".equals(strings[0])) {
			strings[0] = "Dec.";
		}
		String result = hr[0];
		for(int i=0;i<strings.length;i++) {
			if(i == 0) {
				result+=",";
			} else {
				result+="-";
			}
			result+=strings[i];
		}
		return result;
	}
	
	/**
	 * 时间格式转化
	 * @param times
	 * @return
	 */
	public static String formatTime(Long times)
	{
		String time;
		long second = times % 60;
		long minute = (times /  60) %  60;
		long hour = minute / 60;
		time = String.format("%02d:%02d:%02d",hour, minute, second);
		return time;
	}
	
	public static int getDiffDays(Date one, Date two) {
		return getDiffDays(one, two, null);
	}
	
	public static int getDiffDays(Date one, Date two, TimeZone timezone) {
		Calendar d1 = Calendar.getInstance();
		d1.setTime(one);
		if(timezone!=null)
			d1.setTimeZone(timezone);
		d1.set(Calendar.HOUR_OF_DAY, 0);
		d1.set(Calendar.MINUTE, 0);
		d1.set(Calendar.SECOND, 0);
		d1.set(Calendar.MILLISECOND, 0);

		Calendar d2 = Calendar.getInstance();
		d2.setTime(two);
		if(timezone!=null)
			d2.setTimeZone(timezone);
		d2.set(Calendar.HOUR_OF_DAY, 0);
		d2.set(Calendar.MINUTE, 0);
		d2.set(Calendar.SECOND, 0);
		d2.set(Calendar.MILLISECOND, 0);

		long diffms = d2.getTime().getTime() - d1.getTime().getTime();
		return (int) (diffms / 1000 / 60 / 60 / 24);
	}
	
	public static Date getZeroTimeOfDate(Date date){
		Calendar calendar= Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static String formatSeconds(Long time) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
		Date date = new Date(time);
		return simpleDateFormat.format(date);
	}
	
	public static String formatDateTime(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		return simpleDateFormat.format(date);
	}
	
	public static Date stringToDate(String time,String format,String timezone) {
		SimpleDateFormat df = new SimpleDateFormat(format); 
		if(timezone != null) {
			df.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		try {
			return df.parse(time);
		} catch (ParseException e) {
			return new Date();
		}
	}
	
	public static Date getDateOfBeforeDays(Date date, int days, int hour, int minute, TimeZone timezone){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(timezone!=null)
			calendar.setTimeZone(timezone);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - days);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE,30);
		return calendar.getTime();
	}
	
	public static Date getDateOfAfterDays(Date date, int days, int hour, int minute, TimeZone timezone){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(timezone!=null)
			calendar.setTimeZone(timezone);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static TimeZone getTimeZone(String areaCode){
		TimeZone timeZone = null;
		if(Area.NIGERIA_CODE.equals(areaCode)){
			timeZone = TimeZone.getTimeZone("GMT+1:00");
		}else if(Area.TANZANIA_CODE.equals(areaCode)){
			timeZone = TimeZone.getTimeZone("GMT+3:00");
		}
		return timeZone;
	}
}
