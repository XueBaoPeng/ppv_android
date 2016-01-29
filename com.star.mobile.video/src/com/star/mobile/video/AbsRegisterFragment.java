package com.star.mobile.video;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.account.RegisterActivity;
import com.star.mobile.video.service.UserService;


public class AbsRegisterFragment extends Fragment {

	protected RegisterActivity registerActivity;
	protected UserService userService;
	protected AccountService accountService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerActivity  = (RegisterActivity)getActivity();
		userService = new UserService();
		accountService = new AccountService(registerActivity);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		StarApplication.mTracker.setScreenName(this.getClass().getSimpleName());
        StarApplication.mTracker.send(new HitBuilders.AppViewBuilder().build());
	}
}
