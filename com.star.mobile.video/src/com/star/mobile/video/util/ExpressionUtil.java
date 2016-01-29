package com.star.mobile.video.util;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.star.mobile.video.R;


public class ExpressionUtil {
	
	public static String getExpressionString(Context context, String str, String zhengze){
		Pattern pattern = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String key = matcher.group();
            return key;
        }
        return null;
	}
	
	public static SpannableString getSpannableString(Context context, String str, String zhengze){
		if(str == null)
			return null;
    	SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
        } catch (Exception e) {
        	Log.e("ExpressionUtil", "parse expression error!", e);
        }
        return spannableString;
	 }
	
	public static int getExpressionResId(String expressionString){
		try{
			Field field = R.drawable.class.getDeclaredField(expressionString);
			int resId = Integer.parseInt(field.get(null).toString());
			return resId;
		}catch(Exception e){
		}
		return -1;
	}
	
	public static void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start) throws Exception{
    	Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            Field field = R.drawable.class.getDeclaredField(key);
			int resId = Integer.parseInt(field.get(null).toString());
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);	
                int end = matcher.start() + key.length();
				Matrix matrix = new Matrix();
				matrix.postScale(0.75f, 0.75f);
				Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				ImageSpan imageSpan = new ImageSpan(context, newBitmap, ImageSpan.ALIGN_BOTTOM);
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_COMPOSING);
                if (end < spannableString.length()) {
                    dealExpression(context,spannableString,  patten, end);
                }
                break;
            }
        }
    }
}