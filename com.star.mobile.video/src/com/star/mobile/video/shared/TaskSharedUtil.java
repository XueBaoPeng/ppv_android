package com.star.mobile.video.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TaskSharedUtil {
	public static void setFirstTaskActivity(Context context, boolean isFirst) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("task_info", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditot = mSharedPreferences.edit();
		mEditot.putBoolean("first_task", isFirst);
		mEditot.commit();
	}
	
	public static boolean isFirstTaskActivity(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("task_info", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("first_task", false);
	}
	
	/**
	 *
	 * @param context
	 */
	public static void setCoinsStatus(Context context, boolean hasCoins) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("task_info", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditot = mSharedPreferences.edit();
		mEditot.putBoolean("has_coins", hasCoins);
		mEditot.commit();
	}
	
	/**
	 * 是否有积分
	 * @param context
	 * @return
	 */
	public static boolean getCoinsStatus(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("task_info", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("has_coins", false);
	}
	

	/**
	 *
	 * @param context
	 */
	public static void setCouponsStatus(Context context, boolean hasCoins) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("task_info", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditot = mSharedPreferences.edit();
		mEditot.putBoolean("has_coupons", hasCoins);
		mEditot.commit();
	}
	
	/**
	 * 是否有兑换券
	 * @param context
	 * @return
	 */
	public static boolean getCouponsStatus(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("task_info", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("has_coupons", false);
	}
	
}
