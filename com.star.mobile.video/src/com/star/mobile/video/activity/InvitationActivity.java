package com.star.mobile.video.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.star.cms.model.Area;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class InvitationActivity extends BaseActivity {
	
	private TextView tvInvitationCode;
	private TextView tvTwoSetpText;
	private TextView tvCouponinfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invitation);
		SharedPreferencesUtil.setSelInvitation(SharedPreferencesUtil.getUserName(InvitationActivity.this), InvitationActivity.this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.invitation_code));
		tvInvitationCode = (TextView) findViewById(R.id.tv_invition);
		User user = SharedPreferencesUtil.getUserInfo(InvitationActivity.this);
		if(user != null) {
			tvInvitationCode.setText(user.getId()+"");
		}
		tvCouponinfo = (TextView) findViewById(R.id.tv_coupon_info);
		tvTwoSetpText = (TextView) findViewById(R.id.tv_step_two_txt);
		String text = getString(R.string.step_two_txt);
		SpannableString ss = new SpannableString(text);
		ss.setSpan(new ForegroundColorSpan(Color.parseColor("#005FCD")), text.length()-12, text.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 20, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvTwoSetpText.setText(ss);
		setCouponInfo();
		
		findViewById(R.id.iv_actionbar_back).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	public void setCouponInfo(){
		String aredCode = SharedPreferencesUtil.getAreaCode(this);
		String currencSymbol = SharedPreferencesUtil.getCurrencSymbol(this);
		if(Area.TANZANIA_CODE.equals(aredCode)){
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol+"1000"));
		} else if(Area.NIGERIA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol+"100"));
		} else if(Area.KENYA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol+"50"));
		} else if(Area.UGANDA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol+"1000"));
		} else if(Area.SOUTHAFRICA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol+"5"));
		} else if(Area.RWANDA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol+"350"));
		} else {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), ""));
		}
	}

}
