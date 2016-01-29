package com.star.mobile.video.changebouquet;

import android.content.Context;

public class ChangebouquetSharedPre extends com.star.util.SharedPreferences{
	
	public final static String VERIFY_PHONE_ERROR_NUMBER="verify_phone_error_number";//验证手机号错误的次数
	
	public ChangebouquetSharedPre(Context context) {
		super(context);
	}
	@Override
	public String getSharedName() {
		return "ChangeBouquet";
	}

	@Override
	public int getSharedMode() {
		return 0;
	}
	
	public int getVerifyPhoneError(){
		return getInt(VERIFY_PHONE_ERROR_NUMBER, 0);
	}
	
	public void setVerifyPhoneError(int value){
		put(VERIFY_PHONE_ERROR_NUMBER, value);
	}

}
