package com.star.mobile.video.smartcard;

import android.content.Context;

public class SmartCardSharedPre extends com.star.util.SharedPreferences{
	
	public final static String BINDCARDEDITTEXT="bindcardedtext";//绑卡出现的输入框
	public final static String FIRST_BIND_STATUS="first_bind_status";//第一次绑卡状态
	public final static String IS_CLICK_BIND="is_click_bind";//是否点击me button
	
	public SmartCardSharedPre(Context context) {
		super(context);
	}
	@Override
	public String getSharedName() {
		return "BindCard";
	}

	@Override
	public int getSharedMode() {
		return 0;
	}
	
	public int getFirstBindStatus(){
		return getInt(FIRST_BIND_STATUS, 0);
	}
	
	public void setFirstBindStatus(int value){
		put(FIRST_BIND_STATUS, value);
	}

	public void setClickMeButton(boolean isClick){
		put(IS_CLICK_BIND, isClick);
	}
	
	public boolean isClickMeButton(){
		return getBoolean(IS_CLICK_BIND, false);
	}
}
