package com.star.mobile.video.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.star.mobile.video.StarApplication;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.model.MenuHandle;

public class PagerTaskUtil {

	private static List<Class<?>> a_Task = new ArrayList<Class<?>>();
	
	public static int getTaskListSize() {
		return a_Task.size();
	}
	
	public static void addToTask(Class<?> clazz){
		Object object = null;
		try {
			object = clazz.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(object != null){
			if(object instanceof Activity && a_Task.size()>0 && a_Task.get(a_Task.size()-1).equals(clazz)){
				return;
			}
			a_Task.add(clazz);
		}
	}
	
	public static void removeFromTask(final Activity context){
		if(a_Task.size()>0){
			try {
				Class<?> c_ = a_Task.get(a_Task.size()-1);
				Object o_ = c_.getConstructor().newInstance();
				if(o_ instanceof Fragment){
					a_Task.remove(a_Task.size()-1);
				}else if(a_Task.get(a_Task.size()-1).equals(context.getClass()) && !context.getClass().equals(HomeActivity.class)){
		    		a_Task.remove(a_Task.size()-1);
		    	}
				if(a_Task.size()>0){
					final Class<?> clazz = a_Task.get(a_Task.size()-1);
						Object object = clazz.getConstructor().newInstance();
						if(object instanceof Activity){
							CommonUtil.startActivityFromLeft(context, clazz);
						}else if(object instanceof Fragment){
							for(Class<?> c : MenuHandle.getMenuItemClass()){
								if(c.equals(clazz)){
									CommonUtil.startActivityFromLeft(context, HomeActivity.class);
									break;
								}
							}
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
	    	}
    	}
	}
	
	public static void clearTask(){
		a_Task.clear();
		if(StarApplication.mChooseAreaActivity!=null){
			StarApplication.mChooseAreaActivity.finish();
		}
	}
	
	public static void removeFromTask(Class<?> clazz){
		Iterator<Class<?>> iter = a_Task.iterator();
        while(iter.hasNext()){
            Class<?> c = iter.next();
            if(c.getSimpleName().equals(clazz.getSimpleName())){
                iter.remove();
            }
        }
	}
	
	public static Class<?> getCurrentClazz(){
		if(a_Task.size()>0)
			return a_Task.get(a_Task.size()-1);
		else
			return null;
	}
}
