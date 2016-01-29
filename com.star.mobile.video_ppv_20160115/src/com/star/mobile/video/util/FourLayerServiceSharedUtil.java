package com.star.mobile.video.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FourLayerServiceSharedUtil {

	/**
	 * 第一次在后台提醒时间
	 */
	public static void setOneRemindTime(Context context,long times) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("remindTime", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditot = mSharedPreferences.edit();
		mEditot.putLong("time", times);
		mEditot.commit();
	}
	
	public static long getOneRemindTime(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("remindTime", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getLong("time", 0);
	}
	
}
