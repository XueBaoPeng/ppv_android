package com.star.mobile.video.util;

import java.util.Locale;

import com.star.mobile.video.shared.SharedPreferencesUtil;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class LanguageUtil {

	public static void switchLanguage(Context context, String language) {
		if(language==null){
			return;
		}
		Resources resources = context.getApplicationContext().getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();
		if (language.equals("sw")) {
			config.locale = new Locale("sw", "TZ");
		} else if(language.equals("fr")){
			config.locale = Locale.FRENCH;
		}else if(language.equals("en")){
			config.locale = Locale.ENGLISH;
		}else{
			config.locale = Locale.ENGLISH;
		}
	
		
		resources.updateConfiguration(config, dm);
		//保存设置的语言
		SharedPreferencesUtil.setLanguage(language, context);
	}
	
	public static String getLocalLanguage(){
		return Locale.getDefault().getLanguage();
	}
}
