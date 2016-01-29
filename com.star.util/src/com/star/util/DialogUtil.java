package com.star.util;

import android.R;
import android.view.View;
import android.view.ViewGroup;

public class DialogUtil {
	
	private static ViewGroup getRootView(View v){
		return (ViewGroup)v.getRootView().findViewById(R.id.content);
	}
	
	public static void showDialog(final View dialog,final View parent,boolean async){
		if(!async){
			getRootView(parent).addView(dialog);
			return ;
		}
		MainUIHandler.handler().post(new Runnable() {
			@Override
			public void run() {
				getRootView(parent).addView(dialog);
			}
		});
	}
	
	public static void closeDialog(final View dialog,boolean async){
		if(!async){
			ViewGroup parent = (ViewGroup)dialog.getParent();
			parent.removeView(dialog);
		}
		
		MainUIHandler.handler().post(new Runnable() {
			@Override
			public void run() {
				ViewGroup parent = (ViewGroup)dialog.getParent();
				parent.removeView(dialog);
			}
		});
	}
}
