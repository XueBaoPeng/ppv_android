package com.star.mobile.video.util.http;

import java.lang.reflect.Type;

import com.star.util.json.JSONUtil;

public class TypeMap {
	
	public static Object string2Object(String str,Object type){
		if(type==null){
			return str;
		}
		if (type instanceof Type) {
			return JSONUtil.getFromJSON(str, (Type) type);
		} else if (type instanceof Class) {
			return JSONUtil.getFromJSON(str, (Class<?>) type);
		} else if (type instanceof Long) {
			return Long.parseLong(str);
		} else if (type instanceof Integer) {
			return Integer.parseInt(str);
		} else if (type instanceof Float) {
			return Float.parseFloat(str);
		} else if (type instanceof Double) {
			return Double.parseDouble(str);
		} else if (type instanceof Boolean) {
			return Boolean.getBoolean(str);
		} else {
			return str;
		}
	}

}
