package com.star.mobile.video.shared;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.star.mobile.video.R;

public class AppInfoSharedUtil {

	public static void setNewVersionApkUrl(Context context,String apkUrl) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("apk_info", Context.MODE_PRIVATE);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("apk_url", apkUrl);
		mEditor.commit();
	}
	
	
	public static String getNewVersionApkUrl(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("apk_info", Context.MODE_WORLD_READABLE);
		String apkUrl = mSharedPreferences.getString("apk_url", null);
		if(apkUrl == null || "".equals(apkUrl) ) {
			apkUrl = context.getString(R.string.apk_url);
		}
		return apkUrl;
	} 
	
	/**
	 * 记录提示升级
	 */
	public static void setNewVersionAlert(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("apk_info", Context.MODE_PRIVATE);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong("alertTime", System.currentTimeMillis());
		mEditor.commit();
	}
	
	public static long getNewVersionAlert(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("apk_info", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getLong("alertTime", 0);
	}
}
