package com.star.util.color;

import java.util.Arrays;

import android.graphics.Color;
import android.util.Log;

import com.star.util.BuildConfig;
import com.star.util.InterpolatorUtil;

public class ColorUtil {
	static final String TAG = ColorUtil.class.getName();
	
	public static float[] rgb2hsb(int rgbR, int rgbG, int rgbB) {
		if(BuildConfig.DEBUG){
			assert 0 <= rgbR && rgbR <= 255;
			assert 0 <= rgbG && rgbG <= 255;
			assert 0 <= rgbB && rgbB <= 255;
		}
		
		int[] rgb = new int[] { rgbR, rgbG, rgbB };
		Arrays.sort(rgb);
		int max = rgb[2];
		int min = rgb[0];

		float hsbB = max / 255.0f;
		float hsbS = max == 0 ? 0 : (max - min) / (float) max;

		float hsbH = 0;
		if (max == rgbR && rgbG >= rgbB) {
			hsbH = (rgbG - rgbB) * 60f / (max - min) + 0;
		} else if (max == rgbR && rgbG < rgbB) {
			hsbH = (rgbG - rgbB) * 60f / (max - min) + 360;
		} else if (max == rgbG) {
			hsbH = (rgbB - rgbR) * 60f / (max - min) + 120;
		} else if (max == rgbB) {
			hsbH = (rgbR - rgbG) * 60f / (max - min) + 240;
		}

		return new float[] { hsbH, hsbS, hsbB };
	}
	
	
	public static int hsb2rgb(float h, float s, float v){   
		float r = 0, g = 0, b = 0;
	    int i = (int) ((h / 60) % 6);
	    float f = (h / 60) - i;
	    float p = v * (1 - s);
	    float q = v * (1 - f * s);
	    float t = v * (1 - (1 - f) * s);
	    switch (i) {
	    case 0:    r = v;   g = t;   b = p;    break;
	    case 1:    r = q;   g = v;   b = p;    break;
	    case 2:    r = p;   g = v;   b = t;    break;
	    case 3:    r = p;   g = q;   b = v;    break;
	    case 4:    r = t;   g = p;   b = v;    break;
	    case 5:    r = v;   g = p;   b = q;    break;
	    }
	    return Color.rgb((int) (r * 255.0), (int) (g * 255.0), (int) (b * 255.0));
	}
	

	
	
	public static int[] get_rgb_colors(float h_from,float h_to,float s ,float v,int count){
		int[] result = new int[count];
		for(int i = 0 ; i < count; i++){
			float h = InterpolatorUtil.linearValue(h_from, h_to, 0, count,i) % 360;
			result[i] = hsb2rgb(h,s,v);
		}
		return result;
	}
}
