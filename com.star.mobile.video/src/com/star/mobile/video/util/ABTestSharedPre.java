package com.star.mobile.video.util;

import android.content.Context;

public class ABTestSharedPre extends com.star.util.SharedPreferences{
	
	public final static String ABTEST="aorbtest";//ab测试
	
	public ABTestSharedPre(Context context) {
		super(context);
	}
	@Override
	public String getSharedName() {
		return "ABTest";
	}

	@Override
	public int getSharedMode() {
		return 0;
	}
	
	public String getABTest(){
		return getString(ABTEST, "");
	}
	
	public void setABTest(String value){
		put(ABTEST, value);
	}

}
