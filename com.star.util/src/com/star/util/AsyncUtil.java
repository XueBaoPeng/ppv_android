package com.star.util;

import android.view.View;

public class AsyncUtil {
	public static void clearViewAsyncRunnable(View view){
		Runnable runnable = (Runnable)view.getTag(R.id.async_data_load_runnable);
		if(runnable != null){
			MainUIHandler.handler().removeCallbacks(runnable);
			view.setTag(R.id.async_data_load_runnable, null);
		}		
	}
	
	public static void postViewAsyncRunnable(View view,Runnable runnable){
		view.setTag(R.id.async_data_load_runnable,runnable);
		MainUIHandler.handler().post(runnable);
	}
}
