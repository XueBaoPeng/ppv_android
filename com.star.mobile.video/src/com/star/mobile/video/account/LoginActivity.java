package com.star.mobile.video.account;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.Area;
import com.star.cms.model.User;
import com.star.cms.model.dto.LogonResult;
import com.star.cms.model.dto.LogonResult.LogonStatus;
import com.star.cms.model.enm.AccountType;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.SendActivationLinkActicity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.model.NETException;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;

import cn.sharesdk.demo.tpl.ThirdLoginActivity;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.twitter.Twitter;

public class LoginActivity extends ThirdLoginActivity{
	private final String TAG = "LoginActivity";

	private EditText etEmailAddress;
	private EditText etPassword;
	private Button skipBtn;
	private Button btnLogin;
	private TextView btnRegister;
	private TextView tvForgetPassword;
	private String userIcoURL;
	private String username;
	private String pwd;
	private AccountType userType;
	// private ImageView ivFacebookButton;
	private LoginButton mLoginButton;
	private CallbackManager mCallbackManager;
	private ProfileTracker mProfileTracker;
	private ImageView ivTwitterButton;
	private String phoneArea;
	private boolean goBack;

	private AreaService areaService;
	private AccountService accountService;
	private long areaId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FacebookSdk.sdkInitialize(getApplicationContext());
		mCallbackManager = CallbackManager.Factory.create();
		setupProfileTracker();
		mProfileTracker.startTracking();
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);
		areaService = new AreaService(this);
		accountService = new AccountService(this);
		goBack = getIntent().getBooleanExtra("relogin", false);
		areaId = SharedPreferencesUtil.getAreaId(this);
		submitAreaId(false);
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		StarApplication.mTracker.setScreenName(this.getClass().getSimpleName());
		StarApplication.mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void thirdLogin(Platform plat, String userID, String userName, String ico, String nickName) {
		username = User.PrefixOfUsr3Party + "#" + userID + "#" + nickName;
		userIcoURL = ico;
		pwd = ico;
		if (plat instanceof Facebook) {
			userType = AccountType.Facebook;
		} else if (plat instanceof Twitter) {
			userType = AccountType.Twitter;
		}
		login();
	}

	private void initView() {
		skipBtn = (Button) findViewById(R.id.bt_skip);
		etEmailAddress = (EditText) findViewById(R.id.et_email_address);
		// ivFacebookButton = (ImageView) findViewById(R.id.facebook);
		mLoginButton = (LoginButton) findViewById(R.id.login_button);
		ivTwitterButton = (ImageView) findViewById(R.id.twitter);
		etPassword = (EditText) findViewById(R.id.et_passowrd);

		etEmailAddress.setTypeface(Typeface.SANS_SERIF);
		etPassword.setTypeface(Typeface.SANS_SERIF);

		btnLogin = (Button) findViewById(R.id.bt_login);
		btnRegister = (TextView) findViewById(R.id.bt_register);
		tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);


		ButtonListener bl = new ButtonListener();
		tvForgetPassword.setOnClickListener(bl);
		btnLogin.setOnClickListener(bl);
		btnRegister.setOnClickListener(bl);
		skipBtn.setOnClickListener(bl);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(bl);
		// ivFacebookButton.setOnClickListener(bl);
		ivTwitterButton.setOnClickListener(bl);

		currentIntent(getIntent());
		mLoginButton.setReadPermissions("user_friends");
		mLoginButton.registerCallback(mCallbackManager, mFacebookCallback);
		etEmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					String digit = "";
					if(SharedPreferencesUtil.getAreaCode(LoginActivity.this).equals(Area.NIGERIA_CODE)) {
						digit = "10";
					} else {
						digit = "9";
					}
					etEmailAddress.setHint(String.format(getString(R.string.email_phone),digit));
				} else {
					etEmailAddress.setHint(getString(R.string.login_input_user));
				}
			}
		});
		
	}

	private void setupProfileTracker() {
		mProfileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
				//有apk安装的情况下就不让走这里
//				boolean isInstallFaceBookApk = PackageUtil.isInstallFaceBook(LoginActivity.this);
//				if (!isInstallFaceBookApk) {
//				}
				if(currentProfile!=null)
					updateUI();
			}
		};
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mProfileTracker.stopTracking();
	}

	private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {
			Log.d(TAG, "onSuccess");
			//有apk安装的情况下就不让走这里
			updateUI();
		}
		@Override
		public void onCancel() {
			Log.d(TAG, "onCancel");
		}

		@Override
		public void onError(FacebookException e) {
			Log.d(TAG, "onError " + e);
			ToastUtil.centerShowToast(LoginActivity.this, getString(R.string.facebook_login_error));
		}
	};
//	private String DEFAULT_PROFILEID = "14745896640853161";
//	private String DEFAULT_PROF_NAME = "Jack Chen";
	private void updateUI() {
		boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
		Profile profile = Profile.getCurrentProfile();
		if (enableButtons && profile != null) {
			username = User.PrefixOfUsr3Party + "#" + profile.getId() + "#" + profile.getName();
			userIcoURL = profile.getId();
			pwd = profile.getId();
			userType = AccountType.Facebook;
			login();
		}else{
			System.out.println("profile="+profile);
		}
//		else if (enableButtons) {
//			username = User.PrefixOfUsr3Party + "#" + DEFAULT_PROFILEID + "#" + DEFAULT_PROF_NAME;
//			userIcoURL = DEFAULT_PROFILEID;
//			pwd = DEFAULT_PROFILEID;
//			userType = AccountType.Facebook;
//		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}

	private void currentIntent(Intent intent) {
		if (intent.getBooleanExtra("isHideSkipBtn", false)) {
			skipBtn.setVisibility(View.INVISIBLE);
		}
		String lastName = SharedPreferencesUtil.getLastUserName(LoginActivity.this);
		/*boolean isPhone = userService.setPhoneOrEmail(null,etEmailAddress,lastName);
		if(isPhone){
			userType = AccountType.PhoneNumber;
		}else{
			userType = AccountType.Tenbre;
		}*/
//		headView.initLayout(isPhone);
		if(lastName != null && !"".equals(lastName) && !lastName.contains(User.PrefixOfUsr3Party)) {
			etEmailAddress.setText(lastName);
		}
		
		if (intent.getBooleanExtra("fill_name_password", false) && !lastName.contains(User.PrefixOfUsr3Party)) {
			etPassword.setText(SharedPreferencesUtil.getPassword(LoginActivity.this));
		}
	}

	private void goRegister() {
		CommonUtil.startActivity(this, RegisterActivity.class);
//		finish();
	}

	private void goLogin() {
		username = getEmailAdd();
		if(CommonUtil.match(Constant.NUMBER_REG, username)) {
			userType = AccountType.PhoneNumber;
			this.phoneArea = CommonUtil.getSelAreaNumber(LoginActivity.this);
		} else {
			userType = AccountType.Tenbre;
		}
		if (TextUtils.isEmpty(username)) {
			ToastUtil.centerShowToast(LoginActivity.this, getString(R.string.email_address_can_not_be_empty));
			return;
		}
		pwd = getPassword();
		if (TextUtils.isEmpty(pwd)) {
			ToastUtil.centerShowToast(LoginActivity.this, getString(R.string.password_not_empty));
			return;
		}
		login();
	}

	
	private void login() {
		CommonUtil.showProgressDialog(LoginActivity.this, null, getString(R.string.logining));
		String userName = null;
		if(userType==AccountType.PhoneNumber) {
			userName = phoneArea + username;
		} else {
			userName = username;
		}
		accountService.login(userName, pwd, TimeZone.getDefault().getID(), ApplicationUtil.getDeviceId(LoginActivity.this), userType, ApplicationUtil.getAppVerison(LoginActivity.this), new OnResultListener<LogonResult>() {

			@Override
			public boolean onIntercept() {
				
				return false;
			}

			@Override
			public void onSuccess(LogonResult logonResult) {
//				CommonUtil.closeProgressDialog();
				String result = "FAILURE";
				if (logonResult != null) {
					if (logonResult.getStatus() == LogonStatus.success) {
						CommonUtil.saveUserInfo(LoginActivity.this, username, pwd, logonResult.getToken());
						initUserData();
						setPhoneAreaNumber();
						calerAllEditText();
//						goHomeActivity();
						SharedPreferencesUtil.clearDeviceId(LoginActivity.this);
						// 彩蛋提醒服务
//						Intent service = new Intent(LoginActivity.this, EggAlertService.class);
//						startService(service);
//						EggAppearService.getAppearProbabilities();
						result = "SUCCESS";
						if(logonResult.getTaskResult()!=null){
							new TaskService(LoginActivity.this).showTaskDialog(LoginActivity.this, logonResult.getTaskResult(), new PromptDialogClickListener() {
								@Override
								public void onConfirmClick() {
									submitAreaId(true);
								}
								@Override
								public void onCancelClick() {
								}
							});
						}else{
							submitAreaId(true);
						}
					} else{
						CommonUtil.closeProgressDialog();
						if (logonResult.getStatus() == LogonStatus.PwdError) {
							ToastUtil.centerShowToast(LoginActivity.this, getString(R.string.password_is_incorrect));
						} else if (logonResult.getStatus() == LogonStatus.userNotExist) {
							if (CommonUtil.match(Constant.NUMBER_REG, username)) {
								ToastUtil.centerShowToast(LoginActivity.this, getString(R.string.phone_number_not_exist));
							} else {
								ToastUtil.centerShowToast(LoginActivity.this, getString(R.string.user_name_does_not_exist));
							}
						} else if (logonResult.getStatus() == LogonStatus.noActivation) {
							Intent intent = new Intent(LoginActivity.this, SendActivationLinkActicity.class);
							intent.putExtra("mail", username);
							CommonUtil.startActivity(LoginActivity.this, intent);
						} else if (logonResult.getStatus() == LogonStatus.defaultPwd) {
							CommonUtil.saveUserInfo(LoginActivity.this, username, pwd, logonResult.getToken());
							setPhoneAreaNumber();
							calerAllEditText();
							CommonUtil.getInstance().showPromptDialog(LoginActivity.this, getString(R.string.tips),
									getString(R.string.do_you_want_to_reset_your_password), getString(R.string.go),
									getString(R.string.later), new PromptDialogClickListener() {
										
										@Override
										public void onConfirmClick() {
											Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
											intent.putExtra("userName", username);
											CommonUtil.startActivity(LoginActivity.this, intent);
											finish();
										}
										
										@Override
										public void onCancelClick() {
											goHomeActivity();
										}
									});
						} else {
							ToastUtil.centerShowToast(LoginActivity.this, getString(R.string.longin_error));
							if (SyncService.getInstance(LoginActivity.this).isDBReady()) {
								calerAllEditText();
								CommonUtil.startActivity(LoginActivity.this, HomeActivity.class);
							}
						}
					}
				} else {
					ToastUtil.centerShowToast(getApplicationContext(), getString(R.string.error_network));
					if (SyncService.getInstance(LoginActivity.this).isDBReady()) {
						CommonUtil.startActivity(LoginActivity.this, HomeActivity.class);
					}
				}
				if (username != null && username.startsWith(User.PrefixOfUsr3Party)) {
					StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
							.setAction(Constant.GA_EVENT_LOGIN_THIRD)
							.setLabel("USERNAME:" + username + "; STATUS:" + result).setValue(1).build());
				}
				
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}
	
	private void submitAreaId(final boolean doJump) {
		new LoadingDataTask() {
			private boolean status = false;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(status){
					if(SyncService.getInstance(LoginActivity.this).needInit()/*&&!FunctionService.doHideFuncation(FunctionType.SimpleVersion)*/){
						SyncService.getInstance(LoginActivity.this).doInit();
					}
					if(doJump)
						goHomeActivity();
				} else {
					if(doJump&&SyncService.getInstance(LoginActivity.this).isDBReady()) {
						goHomeActivity();
					}
				}
			}
			
			@Override
			public void doInBackground() {
				try {
					areaService.updateUserArea(areaId);
				} catch (NETException e) {
					status = false;
				}
				status = true;
				
			}
		}.execute();
	}
	
	private void initUserData() {
		String uName = SharedPreferencesUtil.getUserName(this);
		if(!TextUtils.isEmpty(uName)) {
			SyncService.getInstance(this).doResetStatus(uName, areaId);
		} else {
			SyncService.getInstance(this).doResetStatus(SharedPreferencesUtil.getDiciveId(this), areaId);
		}
	}

	private void setPhoneAreaNumber() {
		if (userType==AccountType.PhoneNumber && phoneArea != null) {
			SharedPreferencesUtil.setPhoneAreaNumber(this, phoneArea);
		}else{
			SharedPreferencesUtil.setPhoneAreaNumber(this, "");
		}
	}

	private void goHomeActivity() {
		if(!goBack){
			CommonUtil.startActivity(LoginActivity.this, HomeActivity.class);
			finish();
		}else{
			CommonUtil.finishActivityAnimation(this);
		}
	}

	private String getEmailAdd() {
		return etEmailAddress.getText().toString();
	}

	private String getPassword() {
		return etPassword.getText().toString();
	}

	private void calerAllEditText() {
		etEmailAddress.getText().clear();
		etPassword.getText().clear();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		CommonUtil.finishActivityAnimation(this);
	}

	private class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.bt_login) {
				goLogin();
			}
			if (v.getId() == R.id.bt_register) {
				goRegister();
			}
			if (v.getId() == R.id.bt_skip) {
				CommonUtil.startActivity(LoginActivity.this, HomeActivity.class);
				finish();
			}
			if (v.getId() == R.id.tv_forget_password) {
				CommonUtil.startActivity(LoginActivity.this, PhoneAndEmailResetPwdActivity.class);
			}
			// if (v.getId() == R.id.facebook) {
			// authorize(new Facebook(LoginActivity.this));
			// }
			if (v.getId() == R.id.twitter) {
				authorize(new Twitter(LoginActivity.this));
			}
			if (v.getId() == R.id.iv_actionbar_back){
				onBackPressed();
			}
		}
	}

	private void getFaceBookHashKey() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.startimes.tenbreme",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String KeyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
				Log.d("KeyHash:", "KeyHash:" + KeyHash);// 两次获取的不一样 此处取第一个的值
				Toast.makeText(this, "FaceBook HashKey:" + KeyHash, Toast.LENGTH_SHORT).show();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
	}


}
