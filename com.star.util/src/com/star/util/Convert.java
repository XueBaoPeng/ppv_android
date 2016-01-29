package com.star.util;

public class Convert {
	public static int ftoi(float f){
		if(f < 0){
			return (int)(f - 0.5f);
		}
		return (int)(f + 0.5f);
	}
	
	public static int dtoi(double d){
		if(d < 0){
			return (int)(d - 0.5);
		}
		return (int)(d + 0.5);
	}
}
