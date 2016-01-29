package com.star.mobile.video.account;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.dto.LogonResult;
import com.star.cms.model.dto.ResetPwdResult;
import com.star.cms.model.enm.AccountType;
import com.star.cms.model.enm.RegisterStatus;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class ResetPasswordActivity extends BaseActivity implements OnClickListener{
	
	private TextView tvEmailAdd;
	private EditText etOldPwd;
	private EditText etNewPwd;
	private EditText etConfirmNewPwd;
	private Button btResetPass;
//	private TextView tvOldPwdMsg;
//	private TextView tvNewPwdMsg;
//	private TextView tvConfirmPwdMsg;
	
	
	private UserService userService;
	private AccountService accountService;
	private boolean isPhone;
	private String verCode;//验证码
	
	private String phoneArea;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userService = new UserService();
		accountService = new AccountService(this);
		setContentView(R.layout.activity_reset_password);
		initView();
	}

	private void initView() {
		tvEmailAdd = (TextView) findViewById(R.id.tv_email);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.reset_password);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		etOldPwd = (EditText) findViewById(R.id.et_old_pwd);
		etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
		etConfirmNewPwd = (EditText) findViewById(R.id.et_confirm_pwd);
//		tvOldPwdMsg = (TextView) findViewById(R.id.tv_old_pwd_msg);
//		tvNewPwdMsg = (TextView) findViewById(R.id.tv_new_pwd_msg);
//		tvConfirmPwdMsg = (TextView) findViewById(R.id.tv_confirm_pwd_msg);
	 
		etOldPwd.setTypeface(Typeface.SANS_SERIF);
		etNewPwd.setTypeface(Typeface.SANS_SERIF);
		etConfirmNewPwd.setTypeface(Typeface.SANS_SERIF);
		etOldPwd.addTextChangedListener(new MyTextWatcher(etOldPwd));
		etNewPwd.addTextChangedListener(new MyTextWatcher(etNewPwd));
		etConfirmNewPwd.addTextChangedListener(new MyTextWatcher(etConfirmNewPwd));
		btResetPass = (Button) findViewById(R.id.bt_reset_password);
		
		EdittextFocusChange efc = new EdittextFocusChange();
		etOldPwd.setOnFocusChangeListener(efc);
		etNewPwd.setOnFocusChangeListener(efc);
		etConfirmNewPwd.setOnFocusChangeListener(efc);
		btResetPass.setOnClickListener(this);
		currentIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}
	
	private void currentIntent(Intent intent) {
		isPhone = intent.getBooleanExtra("isPhone", false);
		verCode = intent.getStringExtra("vercode");
		String userName = intent.getStringExtra("userName");
		phoneArea = SharedPreferencesUtil.getPhoneAreaNumber(this);
		if(isPhone) {
			etOldPwd.setVisibility(View.GONE);
//			tvOldPwdMsg.setVisibility(View.GONE);
			tvEmailAdd.setText(userName);
		} else {
			etOldPwd.setVisibility(View.VISIBLE);
//			tvOldPwdMsg.setVisibility(View.VISIBLE);
			String userNameFromLocal = SharedPreferencesUtil.getUserName(this);
			if(phoneArea!=null&&!phoneArea.isEmpty()){
				tvEmailAdd.setText(phoneArea+userNameFromLocal);
			}else{
				tvEmailAdd.setText(userNameFromLocal);
			}
		}
	}
	
	private void submitNewPwd() {
		if(checkPwd()) {
			accountService.resetPasswrod(etOldPwd.getEditableText().toString(), getPassword(),getUserName(), new OnResultListener<RegisterStatus>() {
				
				@Override
				public void onSuccess(RegisterStatus result) {
					CommonUtil.closeProgressDialog();
					if(result != null) {
						if(result.equals(RegisterStatus.success)) {
							//修改成功
							CommonUtil.startActivity(ResetPasswordActivity.this, LoginActivity.class);
							finish();
						} else if (result.equals(RegisterStatus.oldPwdError)){
							//旧密码输入错误
							CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
									getString(R.string.incorrect_password), getString(R.string.ok),
									null, null);
						} else if (result.equals(RegisterStatus.userNotExists)) {
							//用户名不存在
							CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
									getString(R.string.user_name_does_not_exist), getString(R.string.ok),
									null, null);
						} else{
							//未知错误
							ToastUtil.centerShowToast(ResetPasswordActivity.this, getString(R.string.password_reset_fails));
						}
					} else {
						ToastUtil.centerShowToast(ResetPasswordActivity.this, getString(R.string.error_network));
					}
				}
				
				@Override
				public boolean onIntercept() {
					return false;
				}
				
				@Override
				public void onFailure(int errorCode, String msg) {
					CommonUtil.closeProgressDialog();
				}
			});
		}
	}
	
	private void resetPhonePwd() {
		if(!checkPwd() ||!checkNewPwd()) {
			return;
		}
		
		accountService.resetPwd(getUserName(),getPassword(), AccountType.PhoneNumber.getNum(), verCode, new OnResultListener<ResetPwdResult>() {

			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(ResetPasswordActivity.this, null, getString(R.string.setting_new_password));
				return false;
			}

			@Override
			public void onSuccess(ResetPwdResult result) {
				CommonUtil.closeProgressDialog();
				if(result != null) {
					if(result.equals(ResetPwdResult.success)) {
//						login();
						
						CommonUtil.startActivity(ResetPasswordActivity.this, LoginActivity.class);	
					} else if(result.equals(ResetPwdResult.userNotExist)) {
						//ToastUtil.centerShowToast(ResetPasswordActivity.this, getString(R.string.user_name_does_not_exist));
						CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
								getString(R.string.user_name_does_not_exist), getString(R.string.ok),
								null, null);
					}
				} else {
					//ToastUtil.centerShowToast(ResetPasswordActivity.this, getString(R.string.password_reset_fails));
					CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
							getString(R.string.password_reset_fails), getString(R.string.ok),
							null, null);
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}
	
	
	private String getUserName() {
		return tvEmailAdd.getText().toString();
	}
	
	private String getOldPassword(){
		return etOldPwd.getEditableText().toString();
	}
	private String getPassword(){
		return etNewPwd.getEditableText().toString();
	}
	
	private void goHomeActivity(LogonResult logonResult) {
		String username = tvEmailAdd.getText().toString();
		if(isPhone||(phoneArea!=null&&!phoneArea.isEmpty())){
			if(username.startsWith("27")){
				SharedPreferencesUtil.setPhoneAreaNumber(this, username.substring(0, 2));
				username = username.substring(2);
			}else{
				SharedPreferencesUtil.setPhoneAreaNumber(this, username.substring(0, 3));
				username = username.substring(3);
			}
		}
		CommonUtil.saveUserInfo(ResetPasswordActivity.this, username, getPassword(), logonResult.getToken());
		CommonUtil.startActivity(ResetPasswordActivity.this, HomeActivity.class);
		finish();
	}
	
	private String getConfirmPwd() {
		return etConfirmNewPwd.getEditableText().toString();
	}
	
	
	private boolean checkNewPwd() {
		
		 if(etOldPwd.getVisibility()!=View.GONE){
			 if(!getOldPassword().equals(SharedPreferencesUtil.getPassword(ResetPasswordActivity.this))){
					CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
							getString(R.string.password_is_incorrect), getString(R.string.ok),
							null, null);
					return false;
				}
		 } 
	 
		if (getPassword().length() < 6) {
//			tvNewPwdMsg.setText(getString(R.string.password_cannot_less_letters));
			CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
					getString(R.string.password_cannot_less_letters), getString(R.string.ok),
					null, null);
			return false;
		}

		if (getPassword().length() > 50) {
//			tvNewPwdMsg.setText(getString(R.string.password_cannoe_greater));
			CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
					getString(R.string.password_cannoe_greater), getString(R.string.ok),
					null, null);
			return false;
		}
		if(!getPassword().matches("^[A-Za-z0-9]+$")) {
//			tvNewPwdMsg.setText(getString(R.string.password_error));
			CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
					getString(R.string.password_error), getString(R.string.ok),
					null, null);
			return false;
		}
		return true;
	}
	
	private boolean checkPwd() {
		
		if(!getPassword().equals(getConfirmPwd())) {
//			tvConfirmPwdMsg.setText(getString(R.string.two_password_do_not_match));
			CommonUtil.getInstance().showPromptDialog(ResetPasswordActivity.this, getString(R.string.tips),
					getString(R.string.two_password_do_not_match), getString(R.string.ok),
					null, null);
			return false;
		}
		return true;
	}
	
	private class MyTextWatcher implements TextWatcher {

		private View view ;
		
		public MyTextWatcher(View view) {
			this.view = view;
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			setPasswordHin(getString(R.string.new_pass));
			setConfirmPasswordHin(getString(R.string.confirm_new_pass));
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		/*	switch (view.getId()) {
			case R.id.et_old_pwd:
					CheckButtonLight();
				break;
				case R.id.et_new_pwd:
					CheckButtonLight();
				break;
				case R.id.et_confirm_pwd:
					CheckButtonLight();
					break;
			default:
				break;
			}*/
			CheckButtonLight();
			setPasswordHin(getString(R.string.new_pass));
			setConfirmPasswordHin(getString(R.string.confirm_new_pass));
		}
	}
	
	private void setPasswordHin(String hint) {
		etNewPwd.setHint(hint);
	}
	
	private void setConfirmPasswordHin(String hint) {
		etConfirmNewPwd.setHint(hint);
	}
	private void CheckButtonLight(){
		if(etOldPwd.getVisibility()==View.GONE){
			if((etNewPwd.getText().toString().length()>0)&&(etConfirmNewPwd.getText().toString().length()>0)){	 
				btResetPass.setBackgroundResource(R.drawable.orange_button_bg);
				btResetPass.setOnClickListener(this);
			}else {
				btResetPass.setBackgroundResource(R.drawable.btn_grey);
				btResetPass.setOnClickListener(null);
			}
		}else{
			
			if((etNewPwd.getText().toString().length()>0)&&(etConfirmNewPwd.getText().toString().length()>0)&&(etOldPwd.getText().toString().length()>0)){	 
				btResetPass.setBackgroundResource(R.drawable.orange_button_bg);
				btResetPass.setOnClickListener(this);
			}else {
				btResetPass.setBackgroundResource(R.drawable.btn_grey);
				btResetPass.setOnClickListener(null);
			}
		}
		
	}
	
	private void  setNewPasswordHin(){
		if(etOldPwd.getVisibility()==View.GONE){
			setPasswordHin(getString(R.string.new_pass));
		}else{
			setPasswordHin(getString(R.string.new_pass));
		}
	}
	private class EdittextFocusChange implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
 
			CheckButtonLight();
			if(v.getId()==R.id.et_old_pwd&&(etOldPwd.getVisibility()==View.GONE)){
				etNewPwd.setFocusable(true);
			}
		 
			if(v.getId() == R.id.et_new_pwd && hasFocus) {
				setPasswordHin(getString(R.string.letter_number_only));
				CheckButtonLight();
			} else if(v.getId() == R.id.et_new_pwd && !hasFocus && ((TextView)v).getText().toString().length() > 0){
				CheckButtonLight();
			} else {
				CheckButtonLight();
				setPasswordHin(getString(R.string.password));
			}
			
			if(v.getId() == R.id.et_confirm_pwd && hasFocus) {
				CheckButtonLight();
				setConfirmPasswordHin(getString(R.string.letter_number_only));

			} else if(v.getId() == R.id.et_confirm_pwd && !hasFocus && ((TextView)v).getText().toString().length() > 0){
				CheckButtonLight();

			} else {
				CheckButtonLight();
				setConfirmPasswordHin(getString(R.string.confirm_password));
			}
			
			if(v.getId() == R.id.et_old_pwd && !hasFocus && ((TextView)v).getText().toString().length() > 0) {
				CheckButtonLight();

			} else if(v.getId() == R.id.et_old_pwd && hasFocus) {
 
				CheckButtonLight();
			} 
			CheckButtonLight();

		} 
		
	}
	/*private OnFocusChangeListener ofc = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) { 
			
			switch (v.getId()) {
			case R.id.et_old_pwd:
				if(hasFocus) {
					setNewPasswordHin(); 
					CheckButtonLight();
				} else {
					setNewPasswordHin(); 
					CheckButtonLight();			
				}
				break;
			case R.id.et_new_pwd:
					if(hasFocus) {
 //						tvNewPwdMsg.setText("");
//						tvConfirmPwdMsg.setText("");
						CheckButtonLight();
						setPasswordHin("At least 6 letters/numbers"); 
					} else {
						// checkNewPwd();
						CheckButtonLight();
						setNewPasswordHin();	
					}
				break;
			case R.id.et_confirm_pwd:
				if(hasFocus) {
//					tvConfirmPwdMsg.setText("");
					CheckButtonLight();
					setNewPasswordHin();
					setConfirmPasswordHin("At least 6 letters/numbers");
				} else {
					//checkPwd();
					CheckButtonLight();
					setNewPasswordHin();
					setConfirmPasswordHin("Confirm New Password");
					
				}
				break;
			
			default:
				break;
			}
		}
	};*/
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_reset_password:
			if(!checkNewPwd()){
				
			}else if(!checkPwd()){
				
			}else{
				if(isPhone) {
					resetPhonePwd();
				} else {
					submitNewPwd();
				}
			}
			
			break;
		case R.id.iv_actionbar_back:
//			CommonUtil.startActivity(ResetPasswordActivity.this, LoginActivity.class);
			finish();
		default:
			break;
		}
		
	}

	
}
