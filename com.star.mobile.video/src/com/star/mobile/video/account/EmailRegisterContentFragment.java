package com.star.mobile.video.account;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.star.cms.model.dto.DoTaskResult;
import com.star.cms.model.dto.RegisterResult;
import com.star.cms.model.enm.AccountType;
import com.star.cms.model.enm.RegisterStatus;
import com.star.mobile.video.AbsRegisterFragment;
import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.activity.SendActivationLinkActicity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.DifferentUrlContral;
import com.star.util.ServerUrlDao;
import com.star.util.loader.OnResultListener;

/**
 * 邮箱注册布局
 * 
 * @author zk
 *
 */
public class EmailRegisterContentFragment extends AbsRegisterFragment implements OnClickListener {

	private View mView;
	private EditText etEmail;
	private EditText etPassword;
	private EditText etComfirmPasswrod;
	private Button btnGo;
	private TextView tvCopyright;
	private Button haveAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
				return mView;
			}
		}
		mView = inflater.inflate(R.layout.fragment_email_register, null);
		initView();
		return mView;
	}

	private void initView() {
		etEmail = (EditText) mView.findViewById(R.id.et_regemail_address);
		etPassword = (EditText) mView.findViewById(R.id.et_reg_password);
		etComfirmPasswrod = (EditText) mView.findViewById(R.id.et_comfim_password);
		btnGo = (Button) mView.findViewById(R.id.bt_emial_reg_go);
		haveAccount = (Button) mView.findViewById(R.id.bt_have_account);
		haveAccount.setOnClickListener(this);
		tvCopyright = (TextView) mView.findViewById(R.id.tv_copyright);
		tvCopyright.setOnClickListener(this);

		btnGo.setOnClickListener(this);
		etEmail.setTypeface(Typeface.SANS_SERIF);
		etPassword.setTypeface(Typeface.SANS_SERIF);
		etComfirmPasswrod.setTypeface(Typeface.SANS_SERIF);

		EdittextFocusChange efc = new EdittextFocusChange();
		etPassword.setOnFocusChangeListener(efc);
		etEmail.setOnFocusChangeListener(efc);
		etComfirmPasswrod.setOnFocusChangeListener(efc);

		etPassword.addTextChangedListener(new MyTextWatcher(etPassword));
		etEmail.addTextChangedListener(new MyTextWatcher(etEmail));
		etComfirmPasswrod.addTextChangedListener(new MyTextWatcher(etComfirmPasswrod));
		String copyright = getString(R.string.copyright);
		SpannableString ss = new SpannableString(copyright);
		ss.setSpan(new ForegroundColorSpan(Color.parseColor("#005FCD")), copyright.length() - 16, copyright.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new UnderlineSpan(), copyright.length() - 16, copyright.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvCopyright.setText(ss);

	}

	private void register() {
		if (checking()) {
			String nickName = android.os.Build.MODEL;
			if (nickName == null || "".equals(nickName)) {
				nickName = "Mobile";
			}
			if (nickName.length() > 20) {
				nickName = nickName.substring(0, 20);
			}
			CommonUtil.showProgressDialog(registerActivity, null, getString(R.string.verifying_your_input));
			accountService.register(getEmailAdd(), getPassword(), nickName,
							AccountType.Tenbre.getNum(), null, null, ApplicationUtil.getAppVerison(registerActivity), new OnResultListener<RegisterResult>() {

								@Override
								public boolean onIntercept() {
									
									return false;
								}

								@Override
								public void onSuccess(RegisterResult registerResult) {
									CommonUtil.closeProgressDialog();
									if (registerResult != null) {
										if (RegisterStatus.success.equals(registerResult.getStatus())) {
											SharedPreferencesUtil.keepUserName(registerActivity, getEmailAdd());
											goActivationPage(registerResult.getTaskResult());
										} else if (RegisterStatus.userExists.equals(registerResult.getStatus())) {
											CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
													getString(R.string.email_has_already_been_registered), getString(R.string.ok),
													null, null);
										} else if (RegisterStatus.other.equals(registerResult.getStatus())) {
											ToastUtil.centerShowToast(registerActivity,
													getString(R.string.sorry) + getString(R.string.registration_failed));
										} else if (RegisterStatus.emailAddressIsWrong.equals(registerResult.getStatus())) {
											ToastUtil.centerShowToast(registerActivity,
													getString(R.string.sorry) + getString(R.string.email_address_is_wrong));
										} else if (RegisterStatus.noActivation.equals(registerResult.getStatus())) {
											goActivationPage(registerResult.getTaskResult());
										} else {
											ToastUtil.centerShowToast(registerActivity,
													getString(R.string.sorry) + getString(R.string.registration_failed));
										}
									} else {
										ToastUtil.centerShowToast(registerActivity,
												getString(R.string.sorry) + getString(R.string.registration_failed));
									}
								}

								@Override
								public void onFailure(int errorCode, String msg) {
									CommonUtil.closeProgressDialog();
								}
							});
		}

	}


	private boolean checkEmail() {
		if (!match(Patterns.EMAIL_ADDRESS, getEmailAdd())) {
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.mailbox_not_correct_format), getString(R.string.ok),
					null, null);
			return false;
		} else if (getEmailAdd().length() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	private boolean checkPassword() {
		if (getPassword().length() < 6) {
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.password_cannot_less_letters), getString(R.string.ok),
					null, null);
			return false;
		} else if (getPassword().length() > 50) {
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.password_cannoe_greater), getString(R.string.ok),
					null, null);
			return false;
		} else if (!getPassword().matches("^[A-Za-z0-9]+$")) {
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.password_error), getString(R.string.ok),
					null, null);
			return false;
		} else if (getPassword().length() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	private boolean checkComfimPassword() {
		if (!(getPassword().equals(getComfimPassword()))) {
			CommonUtil.getInstance().showPromptDialog(registerActivity, getString(R.string.tips),
					getString(R.string.two_password_do_not_match), getString(R.string.ok),
					null, null);
			return false;
		} else {
			return true;
		}
	}

	private boolean checking() {

		return  checkEmail() && checkPassword() && checkComfimPassword();
	}

	private void setPasswordHin(String hint) {
		etPassword.setHint(hint);
	}

	private void setConfirmPasswordHin(String hint) {
		etComfirmPasswrod.setHint(hint);
	}

	private String getEmailAdd() {
		return etEmail.getText().toString();
	}

	private String getPassword() {
		return etPassword.getText().toString();
	}

	private String getComfimPassword() {
		return etComfirmPasswrod.getText().toString();
	}

	private boolean match(Pattern pattern, String str) {
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public class EdittextFocusChange implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			checkButtonLight();
			if (v.getId() == R.id.et_reg_password && hasFocus) {
				setPasswordHin(getString(R.string.letter_number_only));
				checkButtonLight();

			} else if (v.getId() == R.id.et_reg_password && !hasFocus
					&& ((TextView) v).getText().toString().length() > 0) {
				checkButtonLight();
			} else {
				checkButtonLight();
				setPasswordHin(getString(R.string.password));
			}

			if (v.getId() == R.id.et_comfim_password && hasFocus) {
				checkButtonLight();
				setConfirmPasswordHin(getString(R.string.letter_number_only));

			} else if (v.getId() == R.id.et_comfim_password && !hasFocus
					&& ((TextView) v).getText().toString().length() > 0) {
				checkButtonLight();

			} else {
				checkButtonLight();
				setConfirmPasswordHin(getString(R.string.confirm_password));
			}

			if (v.getId() == R.id.et_regemail_address && !hasFocus
					&& ((TextView) v).getText().toString().length() > 0) {
				checkButtonLight();

			} else if (v.getId() == R.id.et_regemail_address && hasFocus) {
				checkButtonLight();
			}
			checkButtonLight();

		}
	}

	private void checkButtonLight() {

		if ((etEmail.getText().toString().length() > 0) && (etPassword.getText().toString().length() > 0)
				&& (etComfirmPasswrod.getText().toString().length() > 0)) {
			btnGo.setBackgroundResource(R.drawable.orange_button_bg);
			btnGo.setOnClickListener(this);
		} else {
			btnGo.setBackgroundResource(R.drawable.btn_grey);
			btnGo.setOnClickListener(null);
		}

	}

	private void goActivationPage(DoTaskResult doTaskResult) {
		Intent intent = new Intent(registerActivity, SendActivationLinkActicity.class);
		intent.putExtra("time", true);
		intent.putExtra("mail", getEmailAdd());
		intent.putExtra("pwd", getPassword());
		intent.putExtra("doTaskResult", doTaskResult);
		CommonUtil.startActivity(registerActivity, intent);
	}

	private class MyTextWatcher implements TextWatcher {

		private View view;

		public MyTextWatcher(View view) {
			this.view = view;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			checkButtonLight();
			switch (view.getId()) {
			case R.id.et_regemail_address:
				break;
			case R.id.et_reg_password:
				break;
			case R.id.et_comfim_password:
				break;
			default:
				break;
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_emial_reg_go:
			if (!checkEmail()) {

			} else if (!checkPassword()) {

			} else if (!checkComfimPassword()) {

			} else {
				register();
			}

			break;
		case R.id.tv_copyright:
			ServerUrlDao serverUrlDao = DifferentUrlContral.diffUrlContral(registerActivity);
			Intent i = new Intent(registerActivity, BrowserActivity.class);
//			i.putExtra("loadUrl", getString(R.string.html_prefix_url) + "/copyright/copyright.html");
			i.putExtra("loadUrl", serverUrlDao.getHtmlPrefixUrl() + "/copyright/copyright.html");
			CommonUtil.startActivity(registerActivity, i);
			break;
		case R.id.bt_have_account:
			Intent have = new Intent(registerActivity, LoginActivity.class);
			CommonUtil.startActivity(registerActivity, have);
			break;
		default:
			break;
		}
	}

}
