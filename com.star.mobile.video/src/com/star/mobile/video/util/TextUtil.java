package com.star.mobile.video.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextUtil {

	private TextUtil() {
	};

	private static TextUtil util;

	public static TextUtil getInstance() {
		if (util == null)
			util = new TextUtil();
		return util;
	}

	/**
	 * 逆序每隔3位添加一个逗号
	 * 
	 * @param str
	 *            :"31232"
	 * @return :"31,232"
	 */
	public String addComma3(String str) {
		str = new StringBuilder(str).reverse().toString(); // 先将字符串颠倒顺序
		String str2 = "";
		for (int i = 0; i < str.length(); i++) {
			if (i * 3 + 3 > str.length()) {
				str2 += str.substring(i * 3, str.length());
				break;
			}
			str2 += str.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		// 最后再将顺序反转过来
		return new StringBuilder(str2).reverse().toString();
	}
	
	public void setText(TextView textview, String text, String key) {
		SpannableStringBuilder style = new SpannableStringBuilder(text);
		if (key != null) {
			int start = text.toLowerCase().indexOf(key.toLowerCase());
			int end = start + key.length();
			if(start >= 0)
				style.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5C05")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		textview.setText(style);
	}
	
	public String encodeText(String text){
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text;
	}
}
