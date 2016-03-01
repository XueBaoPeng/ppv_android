package com.star.mobile.video.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.dto.ResetPwdResult;
import com.star.cms.model.enm.AccountType;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.CheckCodeView;
import com.star.mobile.video.view.CheckCodeView.CheckCodeButtonOnClick;
import com.star.mobile.video.view.CheckCodeView.CodeCallBack;
import com.star.mobile.video.view.PhoneAndEmailView;
import com.star.mobile.video.view.PhoneAndEmailView.CallBack;
import com.star.mobile.video.view.PhoneNumberInputView;
import com.star.util.loader.OnResultListener;

import java.util.regex.Matcher;

public class PhoneAndEmailResetPwdActivity extends BaseActivity implements OnClickListener{
	
	private PhoneAndEmailView headView;
	private LinearLayout llPhoneView;
	private LinearLayout llEmailView;
//	private ImageView ivSuccessIcon;
	private Button btPhoneNext;
	private EditText etEmail;
	private Button btEmailNext;
	private PhoneNumberInputView phoneInputView;
	private CheckCodeView checkCodeView;
//	private TextView tvEmailErrorMsg;
	  
	private UserService userService;
	
	private String checkNumber;//验证码
	
	private AccountService accountService;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_email_reset_password);
		userService = new UserService();
		accountService = new AccountService(this);
		initView();
	}
	
	private void initView() {
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(R.string.reset_password);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		phoneInputView = (PhoneNumberInputView) findViewById(R.id.phone_input);
		headView = (PhoneAndEmailView) findViewById(R.id.head_view);
		llPhoneView = (LinearLayout) findViewById(R.id.ll_phone_view);
		llEmailView = (LinearLayout) findViewById(R.id.ll_email_view);
//		ivSuccessIcon = (ImageView) findViewById(R.id.iv_success_icon);
		btPhoneNext = (Button) findViewById(R.id.bt_phone_next);
		etEmail = (EditText) findViewById(R.id.et_email);
		btEmailNext = (Button) findViewById(R.id.bt_email_next);
//		tvEmailErrorMsg = (TextView) findViewById(R.id.tv_email_error_msg);
		checkCodeView = (CheckCodeView) findViewById(R.id.check_code_view);
		phoneInputView.setPhoneNumberOnFocusChangeListener(new PhoneNumberOnFouceChanged());
		phoneInputView.setPhoneNumberChangedListener(new PhoneNumberOnChanged());
		etEmail.addTextChangedListener(new EmailEdittextChange());
		btEmailNext.setOnClickListener(this);
		if(CommonUtil.getSelAreaNumber(PhoneAndEmailResetPwdActivity.this)==null){
			 headView.GonePhoneView();
			 
		}
		checkCodeView.setCheckCodeButtonOnClick(new CheckCodeButtonOnClick() {
			
			@Override
			public void onClick() {
				sendCode();
			}
		});
		checkCodeView.setCodeCallBack(new CodeCallBack() {
			
			@Override
			public void Listener(String codeText) {
				if(codeText.length() == 4) {
					checkNumber = codeText;
					checkVerCode();
				} else {
					btPhoneNext.setBackgroundResource(R.drawable.btn_grey);
					btPhoneNext.setOnClickListener(null);
				}
			}
		});
		headView.setEmailCallBack(new CallBack() {
			
			@Override
			public void onClick() {
				selectEmail();
			}
		});
		headView.setPhoneCallBack(new CallBack() {
			
			@Override
			public void onClick() {
				selectPhone();
			}
		});
		String lastName = SharedPreferencesUtil.getLastUserName(this);
		boolean isPhone = userService.setPhoneOrEmail(phoneInputView,etEmail,lastName);
		headView.initLayout(isPhone);
	}

	private void selectEmail () {
//		ivSuccessIcon.setVisibility(View.GONE);
		llPhoneView.setVisibility(View.GONE);
		llEmailView.setVisibility(View.VISIBLE);
	}

	private void selectPhone () {
		if(checkPhoneNumber(phoneInputView.getPhoneNumber())) {
//			ivSuccessIcon.setVisibility(View.VISIBLE);
		} else {
//			ivSuccessIcon.setVisibility(View.GONE);
		}
		llPhoneView.setVisibility(View.VISIBLE);
		llEmailView.setVisibility(View.GONE);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.bt_phone_next:
			Intent intent = new Intent(this,ResetPasswordActivity.class);
			intent.putExtra("isPhone", true);
			intent.putExtra("vercode",checkNumber);
			intent.putExtra("userName", phoneInputView.getSelAreaNumber()+phoneInputView.getPhoneNumber());
			CommonUtil.startActivity(this, intent);
			checkCodeView.stopTime();
			checkCodeView.clearCodeEdittext();
			break;
		case R.id.bt_email_next:
//			Intent i = new Intent(this,ResetPasswordActivity.class);
//			i.putExtra("userName", etEmail.getText().toString());
//			CommonUtil.startActivity(this, i);
			if(matchEmail(etEmail.getText().toString())){
				commitEamil();	
			}else{
				CommonUtil.getInstance().showPromptDialog(PhoneAndEmailResetPwdActivity.this, getString(R.string.tips),
						getString(R.string.plese_enter_a_valid_email_address), getString(R.string.ok),
						null, null);
			}
			break;
		default:
			break;
		}
	}
	
	private void commitEamil() {
		final String eamil = etEmail.getText().toString();
		if (!matchEmail(eamil)) {
//			tvEmailErrorMsg.setText(getString(R.string.plese_enter_a_valid_email_address));
			CommonUtil.getInstance().showPromptDialog(PhoneAndEmailResetPwdActivity.this, getString(R.string.tips),
					getString(R.string.plese_enter_a_valid_email_address), getString(R.string.ok),
					null, null);
			return;
		}
		//服务器验证邮箱
		CommonUtil.showProgressDialog(PhoneAndEmailResetPwdActivity.this, null, "Resetting password!");
		accountService.forWordPassword(eamil, new OnResultListener<ResetPwdResult>() {
			
			@Override
			public void onSuccess(ResetPwdResult result) {
				CommonUtil.closeProgressDialog();
				if(result == ResetPwdResult.success) {
					CommonUtil.getInstance().showPromptDialog(PhoneAndEmailResetPwdActivity.this,getString(R.string.tips), String.format(getString(R.string.check_your_email), eamil),getString(R.string.ok), null,new PromptDialogClickListener() {
						
						@Override
						public void onConfirmClick() {
							Intent intent = new Intent(PhoneAndEmailResetPwdActivity.this, LoginActivity.class);
							intent.putExtra("isPhone", false);
							CommonUtil.startActivity(PhoneAndEmailResetPwdActivity.this, intent);
							CommonUtil.finishActivity(PhoneAndEmailResetPwdActivity.this);
						}
						
						@Override
						public void onCancelClick() {
							
						}
					} );
					
				} else if(result == ResetPwdResult.emailAddressIsWrong) {
					ToastUtil.centerShowToast(PhoneAndEmailResetPwdActivity.this, getString(R.string.email_address_is_wrong));
				} else if(result == ResetPwdResult.userNotExist) {
					ToastUtil.centerShowToast(PhoneAndEmailResetPwdActivity.this, getString(R.string.email_address_has_not_been));
				} else {
					ToastUtil.centerShowToast(PhoneAndEmailResetPwdActivity.this, getString(R.string.password_reset_failed));
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
	
	private boolean matchEmail(String str) {
		Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(str);
		 
		return matcher.matches();
	}
	
	private void sendCode() {
		String phone = phoneInputView.getPhoneNumber();
		if(!checkPhoneNumber(phone)) {
			//ToastUtil.centerShowToast(this, getString(R.string.confirm_number));
//			phoneInputView.setErrorText(getString(R.string.number_you_entered_was_not_recognized));
//			phoneInputView.setErrorTextColor(getResources().getColor(R.color.red));
			CommonUtil.getInstance().showPromptDialog(PhoneAndEmailResetPwdActivity.this, getString(R.string.tips),
					getString(R.string.number_you_entered_was_not_recognized), getString(R.string.ok),
					null, null);
			return;
		}
		checkCodeView.time();
		
		accountService.resetPwdSendCode(phoneInputView.getSelAreaNumber()+phoneInputView.getPhoneNumber(),AccountType.PhoneNumber.getNum(), new OnResultListener<ResetPwdResult>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(ResetPwdResult result) {
				String r = "FAILURE";
				if(result != null) {
					if(result.equals(ResetPwdResult.userNotExist)) {
						//ToastUtil.centerShowToast(PhoneAndEmailResetPwdActivity.this, getString(R.string.number_is_not_registered));
 						CommonUtil.getInstance().showPromptDialog(PhoneAndEmailResetPwdActivity.this, getString(R.string.tips),
 								getString(R.string.number_is_not_registered), getString(R.string.ok),
 								null, null);
						r = "USER_NOT_EXIST";
						
					} else if(result.equals(ResetPwdResult.success)) {
						ToastUtil.centerShowToast(PhoneAndEmailResetPwdActivity.this, getString(R.string.please_check));
						//CommonUtil.getInstance().promptDialog(PhoneAndEmailResetPwdActivity.this,R.string.tips, R.string.please_check,R.string.ok,0 );

						r = "SUCCESS";
					} 
				} else {
					ToastUtil.centerShowToast(PhoneAndEmailResetPwdActivity.this, getString(R.string.error_network));
				}
				StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
						.setAction(Constant.GA_EVENT_CODE_RESET_PWD).setLabel("PHONE:"+phoneInputView.getSelAreaNumber()+phoneInputView.getPhoneNumber()+"; STATUS:"+r).setValue(1).build());
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 电话号码不让修改
	 */
	private void setEtPhoneEnable(){
		phoneInputView.setEtPhoneEnable();
	}/**
	 * 电话号码让修改
	 */
	private void setEtPhoneEdit(){
		phoneInputView.setEtPhoneEdit();
	}
	
	private void checkVerCode() {
		final String phoneNumber = phoneInputView.getPhoneNumber();
		final String areaNumber = phoneInputView.getSelAreaNumber();
		if(!checkPhoneNumber(phoneNumber)) {
			return;
		}
		CommonUtil.showProgressDialog(this);
		accountService.checkVerifCode(areaNumber+phoneNumber, checkNumber, new OnResultListener<Boolean>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(Boolean result) {
				CommonUtil.closeProgressDialog();
				if(result != null && result) {
					setEtPhoneEnable();
					btPhoneNext.setBackgroundResource(R.drawable.orange_button_bg);
					btPhoneNext.setOnClickListener(PhoneAndEmailResetPwdActivity.this);
				} else {
					setEtPhoneEdit();
					checkCodeView.setCodeErrorMsg(getResources().getString(R.string.code_error));
					checkCodeView.setCodeErrorTextColor(getResources().getColor(R.color.check_mob_tex));
					CommonUtil.getInstance().showPromptDialog(PhoneAndEmailResetPwdActivity.this, getString(R.string.tips),
								getString(R.string.code_error), getString(R.string.ok),
								null, null);
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
			}
		});
	}
	
	/**
	 * 验证 手机号码位数
	 * @param phoneNumber
	 * @return
	 */
	private boolean checkPhoneNumber(String phoneNumber) {
		String selAreaNumber = phoneInputView.getSelAreaNumber();
		if(getString(R.string.nijeria_number).equals(selAreaNumber)) {
			if(phoneNumber.length() == 10 || (phoneNumber.length()==11&&phoneNumber.startsWith("0"))) {
				return true;
			} else {
				return false;
			}
		} else if(getString(R.string.tanzania_number).equals(selAreaNumber)
				|| getString(R.string.kenya_area_number).equals(selAreaNumber)
				|| getString(R.string.uganda_number).equals(selAreaNumber)
				|| getString(R.string.south_africa_number).equals(selAreaNumber)
				|| getString(R.string.rwanda_number).equals(selAreaNumber)) {
			if(phoneNumber.length() == 9 || (phoneNumber.length()==10&&phoneNumber.startsWith("0"))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private class PhoneNumberOnFouceChanged implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			phoneInputView.setErrorText("");
		}
	}
	
	private class PhoneNumberOnChanged implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			phoneInputView.setErrorText("");
			if(checkPhoneNumber(phoneInputView.getPhoneNumber())) {
//				ivSuccessIcon.setVisibility(View.VISIBLE);
			} else {
//				ivSuccessIcon.setVisibility(View.GONE);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
	} 
	
	private class EmailEdittextChange implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			if(arg0.length() > 0) {
					btEmailNext.setBackgroundResource(R.drawable.orange_button_bg);
 					btEmailNext.setOnClickListener(PhoneAndEmailResetPwdActivity.this);
				 
			}else{
				btEmailNext.setBackgroundResource(R.drawable.btn_grey);
				btEmailNext.setOnClickListener(null);
			}
		}
		
	}

}
