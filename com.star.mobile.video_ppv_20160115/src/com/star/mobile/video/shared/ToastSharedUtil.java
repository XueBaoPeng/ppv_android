package com.star.mobile.video.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ToastSharedUtil {
	
	private static final String TOAST = "toast";
	
	
	public static void setCurrentTime(Context context,long time) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(TOAST, Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong("tokenfailure", time);
		mEditor.commit();
	}

	
	public static long getLastTime(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(TOAST, Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getLong("tokenfailure", 0);
	}
}
