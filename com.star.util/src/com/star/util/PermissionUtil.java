package com.star.util;

import android.os.Looper;

public class PermissionUtil {
	public static void checkInMainThread(){
		if(!isInMainThread()){
			throw new RuntimeException("you should call the method in main ui thread");
		}
	}
	
	public static void checkNotInMainThread(){
		if(isInMainThread()){
			throw new RuntimeException("you should call the method not in main ui thread");
		}
	}
	
	public static boolean isInMainThread(){
		return Looper.getMainLooper() == Looper.myLooper();
	}
}
