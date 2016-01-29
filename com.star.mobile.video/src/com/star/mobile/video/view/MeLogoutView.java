package com.star.mobile.video.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.star.mobile.video.R;
import com.star.mobile.video.ServiceHandler;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.activity.WelcomeActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;

public class MeLogoutView extends LinearLayout implements OnClickListener {
	
	private Context mContext;
	
	public MeLogoutView(Context context) {
		this(context, null);
	}

	public MeLogoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		FacebookSdk.sdkInitialize(context);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.me_logout_view, this);
		findViewById(R.id.lv_logout).setOnClickListener(this);
	}
	
	private void logout() {
		AccountService accountService = new AccountService(mContext);
		SharedPreferencesUtil.clearUserInfo(getContext());
		((StarApplication)getContext().getApplicationContext()).exit();
		CommonUtil.startActivity((Activity)getContext(), WelcomeActivity.class);
		ServiceHandler.getInstance().clearAllService();
		LoginManager.getInstance().logOut();
		accountService.logout(null);
	}

	@Override
	public void onClick(View v) {
		CommonUtil.getInstance().showPromptDialog(mContext, null, mContext.getString(R.string.confirm_to_log_out),
			mContext.getString(R.string.ok), mContext.getString(R.string.later), new PromptDialogClickListener() {
				
				@Override
				public void onConfirmClick() {
					logout();
				}
				
				@Override
				public void onCancelClick() {
					// TODO Auto-generated method stub
					
				}
		});
	}

}
