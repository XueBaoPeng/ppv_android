package com.star.mobile.video.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;

public class MenuHandle {
	private final static Map<Object, Class<?>> clazz_map = new HashMap<Object, Class<?>>();
	private final static Map<String, Drawable> logo_map = new HashMap<String, Drawable>();
	private final static Map<String, String> tag_map = new HashMap<String, String>();

	public static Class<?> getMenuItemClass(Object itemname) {
		return clazz_map.get(itemname);
	}
	
	public static void setMenuItemClass(String itemname, Class<?> clazz) {
		clazz_map.put(itemname, clazz);
	}
	
	public static Collection<Class<?>> getMenuItemClass(){
		return clazz_map.values();
	}
	
	public static Drawable getFragmentLogo(String fragmentTag){
		return logo_map.get(fragmentTag);
	}
	
	public static void setFragmentLogo(String fragmentTag, Drawable drawable){
		logo_map.put(fragmentTag, drawable);
	}
	
	public static String getFragmentTag(String pageName){
		return tag_map.get(pageName);
	}
	
	public static void setFragmentTag(String pageName, String fragmentTag){
		tag_map.put(pageName, fragmentTag);
	}
	
}
