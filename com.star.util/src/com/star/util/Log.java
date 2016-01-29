package com.star.util;

public class Log {
	public static void d(String tag,String msg){
		log(android.util.Log.DEBUG, tag, msg, null);
	}
	
	public static void d(String tag,String msg,Throwable tr){
		log(android.util.Log.DEBUG, tag, msg, tr);
	}
	
	public static void e(String tag,String msg){
		log(android.util.Log.ERROR, tag, msg, null);
	}
	
	public static void e(String tag,String msg,Throwable tr){
		log(android.util.Log.ERROR, tag, msg, tr);
	}
	
	public static void i(String tag,String msg){
		log(android.util.Log.INFO, tag, msg, null);
	}
	
	public static void i(String tag,String msg,Throwable tr){
		log(android.util.Log.INFO, tag, msg, tr);
	}
	
	public static void v(String tag,String msg){
		log(android.util.Log.VERBOSE, tag, msg, null);
	}
	public static void v(String tag,String msg,Throwable tr){
		log(android.util.Log.VERBOSE, tag, msg, tr);
	}
	
	public static void w(String tag,String msg){
		log(android.util.Log.WARN, tag, msg, null);
	}
	
	public static void w(String tag,String msg,Throwable tr){
		log(android.util.Log.WARN, tag, msg, tr);
	}
	
	private static void log(int priority,String tag,String msg,Throwable tr){
		if(tr != null){
			msg = msg + "\n" + android.util.Log.getStackTraceString(tr);
		}
		android.util.Log.println(priority, tag, msg);
	}
}