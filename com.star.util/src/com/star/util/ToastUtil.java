package com.star.util;

import android.view.View;

public class ToastUtil {
	public static void Toast(final View toastView,long durtaion){
		Runnable callback = (Runnable)toastView.getTag(R.id.toast_callback);
		if(callback != null){
			MainUIHandler.handler().removeCallbacks(callback);
		}
		toastView.setVisibility(View.VISIBLE);
		
		callback = new Runnable() {
			@Override
			public void run() {
				toastView.setVisibility(View.INVISIBLE);
				toastView.setTag(R.id.toast_callback, null);
			}
		};
		toastView.setTag(R.id.toast_callback,callback);
		MainUIHandler.handler().postDelayed(callback, durtaion);
	}
}
