package com.star.mobile.video.chatroom.faq;

import android.content.Context;

import com.star.util.SharedPreferences;

public class FAQCacheService extends SharedPreferences {
	
	public static final String FAQ_VERSION = "faq_version";

	public FAQCacheService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSharedName() {
		// TODO Auto-generated method stub
		return "faq";
	}

	@Override
	public int getSharedMode() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getFAQVersion(){
		return getInt(FAQ_VERSION, 0);
	}
	
	public void setFAQVersion(int version){
		put(FAQ_VERSION, version);
	}

}
