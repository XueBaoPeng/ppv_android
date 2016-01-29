package com.star.mobile.video.activity;

import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.star.cms.model.dto.DoTaskResult;
import com.star.cms.model.dto.LogonResult;
import com.star.cms.model.enm.RegisterStatus;
import com.star.mobile.video.R;
import com.star.mobile.video.account.LoginActivity;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.model.MyTimer;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;

/**
 * 发送激活链接
 * @author zk
 *
 */
public class SendActivationLinkActicity extends BaseActivity implements OnClickListener{
	
	private TextView tvMail;
	private Button btActivated;
	private Button btResend;
	private MyTimer myTimer;
	private final int updateTime= 22;
	private int timeCount;
	private boolean isTime;
	private UserService userService;
	private String email;
	private String pwd;
	private DoTaskResult doTaskResult;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == updateTime) {
				if(timeCount <= 0) {
					stopTime();
				} else {
					btResend.setText(timeCount+"s");
					timeCount--;
				}
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_activation_link);
		userService = new UserService();
		initView();
		currentIntent(getIntent());
	}

	private void initView() {	
		tvMail = (TextView) findViewById(R.id.tv_email);
		btActivated = (Button) findViewById(R.id.bt_activated);
		btResend = (Button) findViewById(R.id.bt_resend);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.reg_title));
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		btActivated.setOnClickListener(this);
		btResend.setOnClickListener(this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}
	
	private void currentIntent(Intent intent) {
		email = intent.getStringExtra("mail");
		pwd = intent.getStringExtra("pwd");
		tvMail.setText(email);
		isTime = intent.getBooleanExtra("time", false);
		doTaskResult = (DoTaskResult) intent.getSerializableExtra("doTaskResult");
		if(isTime) {
			time();
		}
	}
	
	private void stopTime() {
		timeCount = 0;
		btResend.setText(getString(R.string.resend));
		btResend.setBackgroundResource(R.drawable.me_modify_bg);
		btResend.setOnClickListener(this);
		btResend.setTextColor(getResources().getColor(R.color.me_modify_btn_bg));
		if(myTimer != null && myTimer.innerTask != null) {
			myTimer.innerTask.cancel();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopTime();
		if(myTimer != null) {
			myTimer.cancel();
		}
	}
	
	/**
	 * 计时
	 */
	private void time() {
		btResend.setTextColor(getResources().getColor(R.color.white));
		btResend.setBackgroundResource(R.drawable.choose_coupon_bg);
		btResend.setOnClickListener(null);
		if(myTimer == null) {
			myTimer = new MyTimer();
		}
		timeCount = 60;
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(updateTime);
			}
		};
		myTimer.innerTask = timerTask;
		myTimer.schedule(timerTask, 0, 1000);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		goLoginActivity();
	}
	
	private void goLoginActivity() {
		CommonUtil.startActivity(SendActivationLinkActicity.this, LoginActivity.class);
		finish();
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bt_resend:
//			time();
//			sendActivationLink();
			break;
		case R.id.bt_activated:
			
			if(doTaskResult!=null){
				new TaskService(this).showTaskDialog(this, doTaskResult, new PromptDialogClickListener() {
					
					@Override
					public void onConfirmClick() {
						goLoginActivity();
					}
					
					@Override
					public void onCancelClick() {
					}
				});
			}else{
				goLoginActivity();
			}
			//login();
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}
	
	/*private void sendActivationLink() {
		
		new LoadingDataTask() {
			private RegisterStatus result;
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(SendActivationLinkActicity.this, null, getString(R.string.sending));
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(result.equals(RegisterStatus.success)) {
					SharedPreferencesUtil.keepUserName(SendActivationLinkActicity.this, email);
					
					CommonUtil.getInstance().showPromptDialog(SendActivationLinkActicity.this, null, getString(R.string.account_from_your_email)+ email, getString(R.string.ok), null, new CommonUtil.PromptDialogClickListener() {
						
						@Override
						public void onConfirmClick() {
							CommonUtil.startActivity(SendActivationLinkActicity.this, MyCouponsActivity.class);
							CommonUtil.finishActivity(SendActivationLinkActicity.this);
						}
						
						@Override
						public void onCancelClick() {
							
						}
					});
					
				} else if(result.equals(RegisterStatus.other)) {
					ToastUtil.centerShowToast(SendActivationLinkActicity.this, getString(R.string.to_resend_the_activation_link_failure));
				} else {
					ToastUtil.centerShowToast(SendActivationLinkActicity.this, getString(R.string.to_resend_the_activation_link_failure));
				}
			}
			
			@Override
			public void doInBackground() {
				result = userService.resendActivationLink(email);
			}
		}.execute();
	}*/
	
	private void goHomeActivity(LogonResult logonResult) {
		CommonUtil.saveUserInfo(SendActivationLinkActicity.this, email, pwd, logonResult.getToken());
		CommonUtil.startActivity(SendActivationLinkActicity.this, HomeActivity.class);
		finish();
	}
}
