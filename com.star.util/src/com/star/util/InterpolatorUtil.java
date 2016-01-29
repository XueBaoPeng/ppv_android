package com.star.util;

public class InterpolatorUtil {
		
	public static float linearValue(float from ,float to ,float start ,float end , float pos ){
		return from + (to - from) * (pos - start) / (end - start);
		
	}
}
