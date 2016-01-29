package com.star.mobile.video.account;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.dto.RegisterResult;
import com.star.cms.model.enm.AccountType;
import com.star.cms.model.enm.RegisterStatus;
import com.star.mobile.video.AbsRegisterFragment;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.me.mycoins.TaskService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

/**
 * 手机号注册布局
 * @author zk
 *
 */
public class MobileRegisterContentFragment extends AbsRegisterFragment implements OnClickListener{
	
	private View mView;
	private EditText etNickName;
	private EditText etPwd;
	private EditText etConfirmPwd;
	private Button btGo;
	
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
		mView = inflater.inflate(R.layout.fragment_mobile_register, null);
		initView();
		return mView;
	}
	
	private void initView() {
		etNickName = (EditText) mView.findViewById(R.id.et_mob_reg_nike_name);
		etPwd = (EditText) mView.findViewById(R.id.et_mob_reg_password);
		etConfirmPwd = (EditText) mView.findViewById(R.id.et_mob_reg_comfim_password);
		
		
		EdittextFocusChange efc = new EdittextFocusChange();
		etNickName.setOnFocusChangeListener(efc);
		etNickName.addTextChangedListener(new TextChange() );
		etPwd.setOnFocusChangeListener(efc);
		etPwd.addTextChangedListener(new TextChange());
		etConfirmPwd.setOnFocusChangeListener(efc);
		etConfirmPwd.addTextChangedListener(new TextChange());
		
		etPwd.setTypeface(Typeface.SANS_SERIF);
		etConfirmPwd.setTypeface(Typeface.SANS_SERIF);
		btGo = (Button) mView.findViewById(R.id.bt_mob_reg_go);
		btGo.setOnClickListener(this);
 
	}
	
	
	private void register() {
		if(checking()) {
			CommonUtil.showProgressDialog(registerActivity, null, getString(R.string.verifying_your_input));
			accountService.register(registerActivity.selAreaNumber+registerActivity.mobileNumber, getPassword(),getNickName(),AccountType.PhoneNumber.getNum(),registerActivity.verifCode,registerActivity.inviteCode,ApplicationUtil.getAppVerison(registerActivity), new OnResultListener<RegisterResult>() {

				@Override
				public boolean onIntercept() {
					return false;
				}

				@Override
				public void onSuccess(final RegisterResult registerResult) {
					CommonUtil.closeProgressDialog();
					String result = "FAILURE";
					if(registerResult != null) {
						if(RegisterStatus.success.equals(registerResult.getStatus())) {
							CommonUtil.saveUserInfo(registerActivity, registerActivity.mobileNumber, getPassword(), SharedPreferencesUtil.getToken(registerActivity));
							result = "SUCCESS";
							CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
									getString(R.string.mob_reg_success), getString(R.string.go),
									null, new PromptDialogClickListener() {
										
										@Override
										public void onConfirmClick() {
											if(registerResult!=null&&registerResult.getTaskResult()!=null){
												new TaskService(registerActivity).showTaskDialog(registerActivity, registerResult.getTaskResult(), new PromptDialogClickListener() {
													
													@Override
													public void onConfirmClick() {
														CommonUtil.startActivity(registerActivity, LoginActivity.class);
														CommonUtil.finishActivity(registerActivity);
													}
													
													@Override
													public void onCancelClick() {
													}
												});
											}else{
												CommonUtil.startActivity(registerActivity, LoginActivity.class);
												CommonUtil.finishActivity(registerActivity);
											}
										}
										
										@Override
										public void onCancelClick() {
										}
									});
						} else if(RegisterStatus.userExists.equals(registerResult.getStatus())) {
							ToastUtil.centerShowToast(registerActivity, getString(R.string.sorry)+getString(R.string.number_has_already_been_registered));
							result = "REGISTERED";
						} else if(RegisterStatus.other.equals(registerResult)) {
							ToastUtil.centerShowToast(registerActivity, getString(R.string.sorry)+getString(R.string.registration_failed));
						} else if(RegisterStatus.emailAddressIsWrong.equals(registerResult.getStatus())) {
							ToastUtil.centerShowToast(registerActivity, getString(R.string.sorry)+getString(R.string.email_address_is_wrong));
							result = "PHONE_NUM_INVALID";
						} else if(RegisterStatus.veriCodeInvalid.equals(registerResult.getStatus())) {
				 			CommonUtil.getInstance().showPromptDialog(registerActivity, null, getString(R.string.code_error), getString(R.string.go), null, new PromptDialogClickListener() {
								
								@Override
								public void onConfirmClick() {
									registerActivity.headView.selPhone();
								}
								
								@Override
								public void onCancelClick() {
									
								}
							});
							result = "CODE_INVALID";
						}else {
							ToastUtil.centerShowToast(registerActivity, getString(R.string.sorry)+getString(R.string.registration_failed));
						}
					} else {
						ToastUtil.centerShowToast(registerActivity, getString(R.string.sorry)+getString(R.string.registration_failed));
					}
					StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
							.setAction(Constant.GA_EVENT_REGISTER_PHONE).setLabel("PHONE:"+registerActivity.selAreaNumber+registerActivity.mobileNumber+"; STATUS:"+result).setValue(1).build());
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					CommonUtil.closeProgressDialog();
				}
			});
		}
	}
	
	
	private boolean checking() {
		if (getPassword().length() < 6) {
			//showToast(getString(R.string.password_cannot_less_letters));
 			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.password_cannot_less_letters), getString(R.string.ok),
					null, null);
			return false;
		}

		if (getPassword().length() > 50) {
			//showToast(getString(R.string.password_cannoe_greater));
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.password_cannoe_greater), getString(R.string.ok),
					null, null);
			return false;
		}
		if(!getPassword().matches("^[A-Za-z0-9]+$")) {
			//showToast(getString(R.string.password_error));
 			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.password_error), getString(R.string.ok),
					null, null);
			return false;
		}

		if (!(getPassword().equals(getComfimPassword()))) {
			//showToast(getString(R.string.two_password_do_not_match));
 			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.two_password_do_not_match), getString(R.string.ok),
					null, null);
			return false;
		}
		
 		/*if (getNickName().trim().length() < 5) {
 		//	showToast(getString(R.string.nickname_can_less_than_five_letters));
 			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.nickname_can_less_than_five_letters), getString(R.string.ok),
					null, null);
 			return false;
 	}*/
 
 		/*if (getNickName().trim().length() > 50) {
 		//	showToast(getString(R.string.less_than_eleven_letters));
 			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.less_than_eleven_letters), getString(R.string.ok),
					null, null);
 			return false;
 		}*/

	
		return true;
	}
	
	private void showToast(String msg) {
		ToastUtil.centerShowToast(registerActivity, msg);
	}
	
	private String getNickName() {
		
		String nickName = android.os.Build.MODEL;
		if(nickName == null) {
			nickName = "Mobile";
		}
		if(nickName.length() > 50) {
			nickName = nickName.substring(0,50);
		}
		return nickName;
//		return etNickName.getText().toString();
	}
	
	private String getPassword() {
		return etPwd.getText().toString();
	}
	
	private String getComfimPassword() {
		return etConfirmPwd.getText().toString();
	}
	
	private void setPasswordHin(String hint) {
		etPwd.setHint(hint);
	}
	
	private void setConfirmPasswordHin(String hint) {
		etConfirmPwd.setHint(hint);
	}
	
	private void setNikeHin(String hint) {
		etNickName.setHint(hint);
	}
	
	private class EdittextFocusChange implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(v.getId() == R.id.et_mob_reg_nike_name && hasFocus) {
				setNikeHin(getString(R.string.five_to_eleven_letters));
			} else {
				setNikeHin(getString(R.string.nick_name));
			}
			
			if(v.getId() == R.id.et_mob_reg_password && hasFocus) {
				setPasswordHin(getString(R.string.letter_number_only));
			} else {
				setPasswordHin(getString(R.string.password));
			}
			
			if(v.getId() == R.id.et_mob_reg_comfim_password && hasFocus) {
				setConfirmPasswordHin(getString(R.string.letter_number_only));
			} else {
				setConfirmPasswordHin(getString(R.string.confirm_password));
			}
		}
	}
	
	private class TextChange implements TextWatcher{

 
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			if((etConfirmPwd.getText().toString().length()>0)&&(etPwd.getText().toString().length()>0)){
				btGo.setBackgroundResource(R.drawable.orange_button_bg);
			}
			
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_mob_reg_go:
			register();
			break;
		default:
			break;
		}
	}
	
}
