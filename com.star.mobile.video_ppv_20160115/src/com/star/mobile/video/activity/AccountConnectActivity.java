package com.star.mobile.video.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.sharesdk.demo.tpl.ThirdLoginActivity;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.twitter.Twitter;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.User;
import com.star.cms.model.dto.BindAccountResult;
import com.star.cms.model.enm.AccountType;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.service.UserService.CallbackListener;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class AccountConnectActivity extends ThirdLoginActivity implements OnClickListener{

	private UserService userService;
	private ImageView facebookBtn;
	private ImageView twitterBtn;
	private int bindAccount = -1;
	private Account facebookAccount;
	private Account twitterAccount;
	private boolean goGetCoin = false;
	private AccountService accountService;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		goGetCoin = getIntent().getBooleanExtra("goGetCoin", false);
		userService = new UserService();
		accountService = new AccountService(this);
		initView();
		facebookAccount = new Account();
		twitterAccount = new Account();
		initBindStatus();
		getAccountStatus();
	}

	private void initBindStatus() {
		boolean fStatus = SharedPreferencesUtil.getBindStatusOf3Account(AccountConnectActivity.this, "Facebook");
		if(fStatus){
			facebookBtn.setImageResource(R.drawable.connect_on);
		}
		boolean tStatus = SharedPreferencesUtil.getBindStatusOf3Account(AccountConnectActivity.this, "Twitter");
		if(tStatus){
			twitterBtn.setImageResource(R.drawable.connect_on);
		}
	}

	private void getAccountStatus() {
		new LoadingDataTask() {
			List<User> users;
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(AccountConnectActivity.this);
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(users!=null && users.size()>0){
					for(User u : users){
						if(AccountType.Facebook.equals(u.getType())){
							facebookBtn.setImageResource(R.drawable.connect_on);
							facebookAccount.user = u;
							facebookAccount.bindStatus = true;
							SharedPreferencesUtil.setBindStatusOf3Account(AccountConnectActivity.this, "Facebook", true);
						}else if(AccountType.Twitter.equals(u.getType())){
							twitterBtn.setImageResource(R.drawable.connect_on);
							twitterAccount.user = u;
							twitterAccount.bindStatus = true;
							SharedPreferencesUtil.setBindStatusOf3Account(AccountConnectActivity.this, "Twitter", true);
						}
					}
				}
			}
			
			@Override
			public void doInBackground() {
				users = userService.getBindedAccounts(StarApplication.mUser.getId());
			}
		}.execute();
	}

	private void initView() {
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.account_connect_title);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		facebookBtn = (ImageView) findViewById(R.id.iv_facebook_connect);
		twitterBtn = (ImageView) findViewById(R.id.iv_twitter_connect);
		facebookBtn.setOnClickListener(AccountConnectActivity.this);
		twitterBtn.setOnClickListener(AccountConnectActivity.this);
		
		if(StarApplication.mUser == null){
			userService.setCallbackListener(new CallbackListener() {
				@Override
				public void callback(User u) {
					if(u == null){
						finish();
					}
					load();
				}
			});
			userService.getUser(this,true);
		}else{
			load();
		}
	}

	private void load() {
		if(AccountType.Facebook.equals(StarApplication.mUser.getType())){
			findViewById(R.id.rl_twitter).setVisibility(View.VISIBLE);
		}else if(AccountType.Twitter.equals(StarApplication.mUser.getType())){
			findViewById(R.id.rl_facebook).setVisibility(View.VISIBLE);
		}else if(AccountType.Tenbre.equals(StarApplication.mUser.getType()) || AccountType.PhoneNumber.equals(StarApplication.mUser.getType())) {
			findViewById(R.id.rl_facebook).setVisibility(View.VISIBLE);
			findViewById(R.id.rl_twitter).setVisibility(View.VISIBLE);
		} else {
			ToastUtil.centerShowToast(this, getString(R.string.account_conection_coming_soon));
			finish();
			return;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.iv_facebook_connect:
			if(!facebookAccount.bindStatus){
				CommonUtil.showProgressDialog(AccountConnectActivity.this);
				authorize(new Facebook(this));
			}else{
				bindAccount = R.id.iv_facebook_connect;
				alertDisconnect("Facebook");
			}
			break;
		case R.id.iv_twitter_connect:
			if(!twitterAccount.bindStatus){
				CommonUtil.showProgressDialog(AccountConnectActivity.this);
				authorize(new Twitter(this));
			}else{
				bindAccount = R.id.iv_twitter_connect;
				alertDisconnect("Twitter");
			}
			break;
		}
	}
	
	@Override
	public void onCancel(Platform platform, int action) {
		super.onCancel(platform, action);
		CommonUtil.closeProgressDialog();
	}
	
	@Override
	public void onError(Platform platform, int action, Throwable t) {
		super.onError(platform, action, t);
		CommonUtil.closeProgressDialog();
	}
	
	private void alertDisconnect(String plat) {
		CommonUtil.getInstance().showPromptDialog(this, null, getString(R.string.account_disconnect_alert,plat), getString(R.string.unlink), getString(R.string.cancel), new PromptDialogClickListener() {
			
			@Override
			public void onConfirmClick() {
				if(bindAccount==R.id.iv_facebook_connect){
					unbindAccount(facebookAccount.user.getUserName(), StarApplication.mUser.getId());
				}else if(bindAccount==R.id.iv_twitter_connect){
					unbindAccount(twitterAccount.user.getUserName(), StarApplication.mUser.getId());
				}
			}
			
			@Override
			public void onCancelClick() {
				bindAccount = -1;
			}
		});
	}

	private void alertConnect(final Platform plat, final String account, final String pwd, final Long parentID) {
		String platStr = null;
		if(plat instanceof Facebook){
			platStr = "Facebook";
		}else if(plat instanceof Twitter){
			platStr = "Twitter";
		}
		CommonUtil.getInstance().showPromptDialog(this, null, getString(R.string.account_connect_alert,platStr), getString(R.string.confirm), getString(R.string.cancel), new PromptDialogClickListener() {
			
			@Override
			public void onConfirmClick() {
				bindAccount(plat, account, pwd, parentID);
			}
			
			@Override
			public void onCancelClick() {
				
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		CommonUtil.finishActivityAnimation(this);
		super.onBackPressed();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		StarApplication.mTracker.setScreenName(this.getClass().getSimpleName());
        StarApplication.mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void thirdLogin(Platform plat, String userID, String userName, String ico,
			String nickName) {
		adjustExist(plat, User.PrefixOfUsr3Party+"#"+userID+"#"+nickName, ico);
	}
	
	private void adjustExist(final Platform plat, final String userName, final String pwd){
		new LoadingDataTask() {
			private User user;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(user!=null){
					if(!user.isValid() || user.getParentID()!=null){
						if(plat instanceof Facebook){
							ToastUtil.centerShowToast(AccountConnectActivity.this, String.format(getString(R.string.account_connect_fail, "Facebook")));
						}else if(plat instanceof Twitter){
							ToastUtil.centerShowToast(AccountConnectActivity.this, String.format(getString(R.string.account_connect_fail, "Twitter")));
						}else{
							ToastUtil.centerShowToast(AccountConnectActivity.this, String.format(getString(R.string.account_connect_fail, "Account")));
						}
					}else{
						alertConnect(plat, userName,  pwd, StarApplication.mUser.getId());
					}
				}else{
					bindAccount(plat, userName, pwd, StarApplication.mUser.getId());
				}
			}
			@Override
			public void doInBackground() {
				user = userService.isExist(userName);
			}
		}.execute();
	}
	
	private void bindAccount(final Platform plat, final String account, final String pwd, final Long parentID) {
		new LoadingDataTask() {
			private BindAccountResult result;
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(AccountConnectActivity.this);
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(result != null){
					User user = result.getUser();
					if(user != null){
						if(plat instanceof Facebook){
							facebookBtn.setImageResource(R.drawable.connect_on);
							facebookAccount.bindStatus = true;
							facebookAccount.user = user;
							SharedPreferencesUtil.setBindStatusOf3Account(AccountConnectActivity.this, "Facebook", true);
						}else if(plat instanceof Twitter){
							twitterBtn.setImageResource(R.drawable.connect_on);
							twitterAccount.bindStatus = true;
							twitterAccount.user = user;
							SharedPreferencesUtil.setBindStatusOf3Account(AccountConnectActivity.this, "Twitter", true);
						}
						ToastUtil.centerShowToast(AccountConnectActivity.this, getString(R.string.account_connect_successful));
//						if(goGetCoin){
//							for(final TaskVO vo : StarApplication.mUndoTask){
//								if(vo.getCode().equals(TaskCode.Link_ThirdAccount)){
//									new Handler().postDelayed(new Runnable() {
//										@Override
//										public void run() {
//											CommonUtil.showTaskCompleteDialog(AccountConnectActivity.this, vo.getName());
//										}
//									}, 1000);
//									break;
//								}
//							}
//						}
					}
					new TaskService(AccountConnectActivity.this).showTaskDialog(AccountConnectActivity.this, result.getTaskResult(), null);
				}else{
					ToastUtil.centerShowToast(AccountConnectActivity.this, getString(R.string.account_connect_unsuccessful));
				}
			}
			
			@Override
			public void doInBackground() {
				if(plat instanceof Facebook){
					result = userService.bindAccount(account, pwd, ApplicationUtil.getDeviceId(AccountConnectActivity.this), parentID, AccountType.Facebook,ApplicationUtil.getAppVerison(AccountConnectActivity.this));
				}else if(plat instanceof Twitter){
					result = userService.bindAccount(account, pwd, ApplicationUtil.getDeviceId(AccountConnectActivity.this), parentID, AccountType.Twitter,ApplicationUtil.getAppVerison(AccountConnectActivity.this));
				}
			}
		}.execute();
	}
	
	private void unbindAccount(final String account, final Long parentID) {
		accountService.unbindAccount(account, parentID, new OnResultListener<Boolean>() {
			
			@Override
			public void onSuccess(Boolean value) {
				CommonUtil.closeProgressDialog();
				if(bindAccount==R.id.iv_facebook_connect){
					facebookBtn.setImageResource(R.drawable.connect_off);
					facebookAccount.bindStatus = false;
					SharedPreferencesUtil.setBindStatusOf3Account(AccountConnectActivity.this, "Facebook", false);
				}else if(bindAccount==R.id.iv_twitter_connect){
					twitterBtn.setImageResource(R.drawable.connect_off);
					twitterAccount.bindStatus = false;
					SharedPreferencesUtil.setBindStatusOf3Account(AccountConnectActivity.this, "Twitter", false);
				}
				ToastUtil.centerShowToast(AccountConnectActivity.this, getString(R.string.account_disconnect_successful));
				bindAccount = -1;
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(AccountConnectActivity.this);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				ToastUtil.centerShowToast(AccountConnectActivity.this, getString(R.string.account_disconnect_unsuccessful));
				bindAccount = -1;
			}
		});
	}
	
	class Account{
		User user;
		boolean bindStatus;
	}
	
}
