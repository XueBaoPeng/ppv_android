package com.star.mobile.video.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.cms.model.dto.ResetPwdResult;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.loader.OnResultListener;

public class ResetPasswordSendMailActivity extends BaseActivity{
	
	private EditText etCheckEmail;
	private TextView tvEamilInfoPrompt;
	private Button btnOk;
	private AccountService accountService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password_send_mail);
		accountService = new AccountService(this);
		initView();
	}
	
	private void initView() {
		etCheckEmail = (EditText) findViewById(R.id.et_check_mail);
		btnOk = (Button) findViewById(R.id.bt_check_ok);
		tvEamilInfoPrompt = (TextView) findViewById(R.id.tv_info);
		ButtonOnClick bc = new ButtonOnClick();
		((ImageView)findViewById(R.id.iv_actionbar_back)).setOnClickListener(bc);
		btnOk.setOnClickListener(bc);
	}
	
	
	private String getEmailText() {
		return etCheckEmail.getText().toString();
		
	}
	
	private void setPromptInfoText(String msg) {
		tvEamilInfoPrompt.setText(msg);
	}
	
	private void setPromptInfoTextColor(int colorId) {
		tvEamilInfoPrompt.setTextColor(colorId);
	}
	
	
	private void commitEamil() {
		String regexEmail = "^([a-z0-9A-Z]+[-_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		if (!match(regexEmail, getEmailText())) {
			setPromptInfoText(getString(R.string.plese_enter_a_valid_email_address));
			setPromptInfoTextColor(getResources().getColor(R.color.red));
			return;
		}
		//服务器验证邮箱
		CommonUtil.showProgressDialog(ResetPasswordSendMailActivity.this, null, "Resetting password!");
		accountService.forWordPassword(getEmailText(), new OnResultListener<ResetPwdResult>() {
			
			@Override
			public void onSuccess(ResetPwdResult result) {
				CommonUtil.closeProgressDialog();
				if(result == ResetPwdResult.success) {
					CommonUtil.getInstance().showPromptDialog(ResetPasswordSendMailActivity.this, null, getString(R.string.check_your_email), getString(R.string.ok), null, new CommonUtil.PromptDialogClickListener() {
						
						@Override
						public void onConfirmClick() {
							CommonUtil.startActivity(ResetPasswordSendMailActivity.this, LoginActivity.class);
							CommonUtil.finishActivity(ResetPasswordSendMailActivity.this);
						}
						
						@Override
						public void onCancelClick() {
							
						}
					});
				} else if(result == ResetPwdResult.emailAddressIsWrong) {
					ToastUtil.centerShowToast(ResetPasswordSendMailActivity.this, getString(R.string.email_address_is_wrong));
				} else if(result == ResetPwdResult.userNotExist) {
					ToastUtil.centerShowToast(ResetPasswordSendMailActivity.this, getString(R.string.email_address_has_not_been));
				} else {
					ToastUtil.centerShowToast(ResetPasswordSendMailActivity.this, getString(R.string.password_reset_failed));
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
	
	private boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	

	private class ButtonOnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_check_ok:
				commitEamil();
				break;
			case R.id.iv_actionbar_back:
				ResetPasswordSendMailActivity.super.onBackPressed();
				break;
			default:
				break;
			}
		}
	}
}
