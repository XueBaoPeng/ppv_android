package com.star.mobile.video.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

public class ApplicationUtil {
	
	public static int getAppVerison(Context context) {
		int result;
		 try {
			PackageInfo appInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			result = appInfo.versionCode;
		} catch (NameNotFoundException e) {
			result = 0;
			e.printStackTrace();
		}
		 return result;
	} 
	
	public static String getAppVerisonName(Context context) {
		String result;
		 try {
			PackageInfo appInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			result = appInfo.versionName;
		} catch (NameNotFoundException e) {
			result = "";
			e.printStackTrace();
		}
		return result;
	} 
	
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		return tm.getDeviceId();
	}
	
	public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks!=null&&!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
	
	public static boolean isPkgInstalled(Context context, String pkgName) {
		try {
			context.getPackageManager().getPackageInfo(pkgName, 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 获取当前正在运行的Activity
	 * @return
	 * 		<uses-permission android:name="android.permission.GET_TASKS"/>
	 */
	public static String getActivityName(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo info = manager.getRunningTasks(1).get(0);
		String shortClassName = info.topActivity.getShortClassName();
		System.out.println("shortClassName=" + shortClassName);
		return shortClassName;
	}
}
