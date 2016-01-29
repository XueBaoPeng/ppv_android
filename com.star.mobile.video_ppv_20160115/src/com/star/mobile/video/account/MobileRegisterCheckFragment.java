package com.star.mobile.video.account;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.dto.PhoneRegisterResult;
import com.star.mobile.video.AbsRegisterFragment;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.dialog.AlertDialog;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DefaultLoadingTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.CheckCodeView;
import com.star.mobile.video.view.CheckCodeView.CheckCodeButtonOnClick;
import com.star.mobile.video.view.CheckCodeView.CodeCallBack;
import com.star.util.loader.OnResultListener;
import com.star.mobile.video.view.PhoneNumberInputView;

/**
 * 填写手机验证码
 * @author zk
 *
 */
public class MobileRegisterCheckFragment extends AbsRegisterFragment implements OnClickListener{

	private View mView;
	private Button btNext;
	private PhoneNumberInputView phoneInputView;
	private CheckCodeView checkCodeView;
	private TextView tvThree;
	private boolean isCodeMsgRed = false;
	private boolean isMobMsgRed;
	private boolean isShowFragment = true;
	private EditText etInviteCode;//邀请码
	private Button haveCount;
	private AccountService mAccountService;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if(parent != null) {
				parent.removeView(mView);
				return mView;
			}
		}
		mView = inflater.inflate(R.layout.fragment_mobile_check, null);
		initView();
		return mView;
	}
	
	private void initView() {
		btNext = (Button) mView.findViewById(R.id.bt_mob_check_next);
		checkCodeView = (CheckCodeView) mView.findViewById(R.id.check_code_view);
		phoneInputView = (PhoneNumberInputView) mView.findViewById(R.id.phone_input);
		tvThree = (TextView) mView.findViewById(R.id.tv_three);
		etInviteCode = (EditText) mView.findViewById(R.id.et_invite_code);
		haveCount=(Button) mView.findViewById(R.id.bt_have_account);
		haveCount.setOnClickListener(this);
		tvThree.setOnClickListener(this);
		etInviteCode.setTypeface(Typeface.SANS_SERIF);
		
		String copyright = getString(R.string.mob_tip_msg_three);
		SpannableString ss = new SpannableString(copyright);
		ss.setSpan(new ForegroundColorSpan(Color.parseColor("#005FCD")), copyright.length()-16, copyright.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new UnderlineSpan(), copyright.length()-16, copyright.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvThree.setText(ss);
		phoneInputView.setErrorTextColor(getResources().getColor(R.color.choose_text));
		phoneInputView.setPhoneNumberChangedListener(new MobTextWatcher());
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
					if(isCodeMsgRed) {
						isCodeMsgRed = false;
					}
					checkCode();
					checkCodeView.setCodeErrorMsg("");
				} else {
					setNextOnClickNull();
				}
				
			}
		});
		
		checkCodeView.setCodeOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				checkCodeView.setCodeErrorMsg(getResources().getString(R.string.code_msg));
				checkCodeView.setCodeErrorTextColor(getResources().getColor(R.color.grey));
				isCodeMsgRed = false;
			}
		});
		
		phoneInputView.setPhoneNumberOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isMobMsgRed = false;
			}
		});
		mAccountService = new AccountService(getActivity());
	}

	@Override
	public void onStart() {
		super.onStart();
		isShowFragment = true;
		setNextOnClickNull();
	}
	
	/**
	 * Next 按钮点击事件设置null
	 */
	private void setNextOnClickNull() {
		btNext.setOnClickListener(null);
		btNext.setBackgroundResource(R.drawable.btn_grey);
	}
	
	private void sendCode() {
		final String phoneNumber = getPhoneNumber();
		 
		if(!checkPhoneNumber(phoneNumber)) {
			//ToastUtil.centerShowToast(registerActivity, registerActivity.getString(R.string.confirm_number));
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.confirm_number), getString(R.string.ok),
					null, null);
			return;
		}
		checkCodeView.time();
		mAccountService.getVerifCode(phoneInputView.getSelAreaNumber()+phoneNumber, new OnResultListener<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				if(isShowFragment) {
					String r = "FAILURE";
					if(result != null) {
						if(result == PhoneRegisterResult.SUCCESS) {
							ToastUtil.centerShowToast(registerActivity, getString(R.string.please_check));
							checkCodeView.inputCodeSel();
							r = "SUCCESS";
						} else if(result == PhoneRegisterResult.TIME_INTERVAL_NOT_ENOUGH) {
							r = "SEND_FREQUEN";
						} else if(result == PhoneRegisterResult.PHONE_NUM_INVALID) {
							phoneInputView.setErrorText(getString(R.string.error_phone_msg));
							phoneInputView.setErrorTextColor(getResources().getColor(R.color.check_mob_tex));
							isMobMsgRed = true;
							checkCodeView.stopTime();
							r = "PHONE_NUM_INVALID";
						} else if(result == PhoneRegisterResult.PHONE_IS_EXIST) { //注册手机号已经存在
							CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
									getString(R.string.su_login), getString(R.string.ok),
									null, null);
							checkCodeView.stopTime();
							r = "PHONE_NUM_EXIST";
						}
					} else {
						ToastUtil.centerShowToast(registerActivity, getString(R.string.error_network));
						r = "NETWORK_ERROR";
					}
					StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
							.setAction(Constant.GA_EVENT_CODE_REGISTER).setLabel("PHONE:"+phoneInputView.getSelAreaNumber()+phoneNumber+"; STATUS:"+r).setValue(1).build());
				}
			}
			
			@Override
			public boolean onIntercept() {
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				
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
	
	
	private void checkCode() {
		final String phoneNum = getPhoneNumber();
		final String verCode = checkCodeView.getCodeText();
		final String areaNumber = phoneInputView.getSelAreaNumber();
		if(phoneNum.trim().length() > 7  && phoneNum.trim().length() < 13 && verCode.trim().length() == 4) {
			
			CommonUtil.showProgressDialog(registerActivity);
			mAccountService.checkVerifCode(areaNumber+phoneNum, verCode,new OnResultListener<Boolean>() {

				@Override
				public boolean onIntercept() {
					return false;
				}

				@Override
				public void onSuccess(Boolean result) {
					CommonUtil.closeProgressDialog();
					if(result != null && result) {
//						checkCodeView.stopTime();
						btNext.setBackgroundResource(R.drawable.orange_button_bg);
						btNext.setOnClickListener(MobileRegisterCheckFragment.this);
						registerActivity.verifCode = verCode;
						registerActivity.mobileNumber = phoneNum;
						registerActivity.selAreaNumber = areaNumber;
					} else {
						isCodeMsgRed = true;
						checkCodeView.setCodeErrorMsg(getResources().getString(R.string.code_error));
						CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
								getString(R.string.code_error), getString(R.string.ok),
								null, null);
						checkCodeView.setCodeErrorTextColor(getResources().getColor(R.color.check_mob_tex));
					}
					
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					CommonUtil.closeProgressDialog();
				}
			});
			
		}else{
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.code_error), getString(R.string.ok),
					null, null);
		}
	}
	
	private void clertEdittext() {
		checkCodeView.clearCodeEdittext();
		phoneInputView.clearPhoneNumber();
	}
	
	
	private String getPhoneNumber() {
		
		return phoneInputView.getPhoneNumber();
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
		checkCodeView.stopTime();
		isShowFragment = false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_mob_check_next:
			goMobRegister();
			break;
		case R.id.tv_three:
			Intent i = new Intent(registerActivity,BrowserActivity.class);
			i.putExtra("loadUrl", getString(R.string.html_prefix_url)+"/copyright/copyright.html");
			CommonUtil.startActivity(registerActivity, i);
			break;
		case R.id.bt_have_account:
			Intent have=new Intent(registerActivity,LoginActivity.class);
			CommonUtil.startActivity(registerActivity, have);
			break;
		default:
			break;
		}
	}
	
	
	private void goMobRegister() {
		registerActivity.setMobileRegisterContentFragment(RegisterActivity.PHONE_REGISTER);	
		//registerActivity.setFragment(RegisterActivity.PHONE_REGISTER);
		registerActivity.inviteCode = etInviteCode.getText().toString();
		registerActivity.isMobileRegister = true;
		clertEdittext();
		checkCodeView.stopTime();
	}
	
	private class MobTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(isMobMsgRed) {
				phoneInputView.setErrorText("");
				phoneInputView.setErrorTextColor(getResources().getColor(R.color.choose_text));
				isMobMsgRed = false;
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		}
		
	}
}
