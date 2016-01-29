package com.star.mobile.video.account;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;

public class ResetFillPassActivity extends BaseActivity{

	private TextView tvEmail;
	private EditText etNewPassword;
	private EditText etConfirmPasswrod;
	private Button btRegister;
	private Button btResetPass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_fill_password);
		initView();
	}
	
	private void initView() {
		tvEmail = (TextView) findViewById(R.id.tv_email);
		etNewPassword = (EditText) findViewById(R.id.et_new_pass);
		etConfirmPasswrod = (EditText) findViewById(R.id.et_confirm_new_pass);
		btRegister = (Button) findViewById(R.id.bt_reset_register);
		btResetPass = (Button) findViewById(R.id.bt_reset_pass);
		
	} 
}
