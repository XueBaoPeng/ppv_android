package com.star.mobile.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.EggService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ShareUtil;

public class TellFriendActivity extends BaseActivity implements OnClickListener {

	public static Activity activity;
	private CallbackManager callbackManager;
	private TextView tvInvitationCode;
	private TextView tvCouponinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		callbackManager = CallbackManager.Factory.create();
		setContentView(R.layout.activity_tell_friend);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.invititions);
		((ImageView) findViewById(R.id.iv_actionbar_search)).setImageResource(R.drawable.ic_share_white);
		((ImageView) findViewById(R.id.iv_actionbar_search)).setOnClickListener(this);
		tvInvitationCode = (TextView) findViewById(R.id.tv_invition);
		tvCouponinfo = (TextView) findViewById(R.id.tv_coupon_info);
		User user = SharedPreferencesUtil.getUserInfo(TellFriendActivity.this);
		if (user != null) {
			tvInvitationCode.setText(user.getId() + "");
		}
		setCouponInfo();
		String aredCode = SharedPreferencesUtil.getAreaCode(TellFriendActivity.this);
		String currencSymbol = SharedPreferencesUtil.getCurrencSymbol(TellFriendActivity.this);
//		if (Area.TANZANIA_CODE.equals(aredCode)) {
//			((TextView) findViewById(R.id.content_one))
//					.setText(String.format(getString(R.string.tell_friend_content_one), currencSymbol + "1000"));
//			// ((TextView)
//			// findViewById(R.id.content_two)).setText(String.format(getString(R.string.tell_friend_content_two),
//			// currencSymbol+"1000*6"));
//			// ((TextView)
//			// findViewById(R.id.content_three)).setText(String.format(getString(R.string.tell_friend_content_three),
//			// currencSymbol+"1000"));
//		} else if (Area.NIGERIA_CODE.equals(aredCode)) {
//			((TextView) findViewById(R.id.content_one))
//					.setText(String.format(getString(R.string.tell_friend_content_one), currencSymbol + "100"));
//			// ((TextView)
//			// findViewById(R.id.content_two)).setText(String.format(getString(R.string.tell_friend_content_two),
//			// currencSymbol+"100*6"));
//			// ((TextView)
//			// findViewById(R.id.content_three)).setText(String.format(getString(R.string.tell_friend_content_three),
//			// currencSymbol+"100"));
//		} else if (Area.KENYA_CODE.equals(aredCode)) {
//			((TextView) findViewById(R.id.content_one))
//					.setText(String.format(getString(R.string.tell_friend_content_one), currencSymbol + "50"));
//			// ((TextView)
//			// findViewById(R.id.content_two)).setText(String.format(getString(R.string.tell_friend_content_two),
//			// currencSymbol+"100*6"));
//			// ((TextView)
//			// findViewById(R.id.content_three)).setText(String.format(getString(R.string.tell_friend_content_three),
//			// currencSymbol+"100"));
//		} else if (Area.UGANDA_CODE.equals(aredCode)) {
//			((TextView) findViewById(R.id.content_one))
//					.setText(String.format(getString(R.string.tell_friend_content_one), currencSymbol + "1000"));
//		} else if (Area.SOUTHAFRICA_CODE.equals(aredCode)) {
//			((TextView) findViewById(R.id.content_one))
//					.setText(String.format(getString(R.string.tell_friend_content_one), currencSymbol + "5"));
//		} else if (Area.RWANDA_CODE.equals(aredCode)) {
//			((TextView) findViewById(R.id.content_one))
//					.setText(String.format(getString(R.string.tell_friend_content_one), currencSymbol + "350"));
//		} else {
			((TextView) findViewById(R.id.content_one))
					.setText(String.format(getString(R.string.tell_friend_content_one), ""));

		activity = this;
	}

	private void setCouponInfo() {
		String aredCode = SharedPreferencesUtil.getAreaCode(this);
		String currencSymbol = SharedPreferencesUtil.getCurrencSymbol(this);
		/*if (Area.TANZANIA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol + "1000"));
		} else if (Area.NIGERIA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol + "100"));
		} else if (Area.KENYA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol + "50"));
		} else if (Area.UGANDA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol + "1000"));
		} else if (Area.SOUTHAFRICA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol + "5"));
		} else if (Area.RWANDA_CODE.equals(aredCode)) {
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), currencSymbol + "350"));
		} else {*/
			tvCouponinfo.setText(String.format(getString(R.string.invita_one), ""));


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.iv_actionbar_search:
			if (SharedPreferencesUtil.getUserName(this) != null) {
				new EggService(this).shareFreeCouponExchange(this, ShareUtil.DIRECTLY_SHARE);
			} else {
				CommonUtil.pleaseLogin(this);
			}
			break;
		/*
		 * case R.id.share_friend_button:
		 * if(SharedPreferencesUtil.getUserName(this) != null) {
		 * EggAppearService.shareFreeCouponExchange(this,ShareUtil.
		 * DIRECTLY_SHARE); } else { CommonUtil.pleaseLogin(this); } break;
		 */
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

}
