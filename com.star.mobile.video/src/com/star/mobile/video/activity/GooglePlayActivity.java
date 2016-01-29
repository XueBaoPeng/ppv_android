package com.star.mobile.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.analytics.HitBuilders;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;

public class GooglePlayActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_googleplay);
		findViewById(R.id.iv_btn_google).setOnClickListener(this);
		findViewById(R.id.iv_btn_dismiss).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String username = StarApplication.mUser==null?SharedPreferencesUtil.getLastUserNameOrDeviceId(this):StarApplication.mUser.toString();
		switch (v.getId()) {
		case R.id.iv_btn_google:
			goGooglePlay(username);
			commitStatus();
			finish();
			break;
		case R.id.iv_btn_dismiss:
			StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
					.setAction(Constant.GA_EVENT_GOOGLE_CANCLE).setLabel("VERSION:"+ApplicationUtil.getAppVerisonName(this)+"; USER:"+username).setValue(1).build());
			finish();
			break;
		}
	}

	private void commitStatus() {
		new Thread(){
			public void run() {
				SyncService.getInstance(GooglePlayActivity.this).StopGoGooglePlayStatus();
			};
		}.start();
	}

	private void goGooglePlay(String username) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.star.mobile.video"));
		String goTo_ = "BROWSER";
		if (ApplicationUtil.isPkgInstalled(this, "com.android.vending")) {
			intent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
			goTo_ = "GOOGLEPLAYAPP";
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
				.setAction(Constant.GA_EVENT_GOOGLE_GO).setLabel("VERSION:"+ApplicationUtil.getAppVerisonName(this)+"; USER:"+username+"; BY "+goTo_).setValue(1).build());
	}
}
