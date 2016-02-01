package com.star.mobile.video.changebouquet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.smartcard.MyOrderActivity;
import com.star.mobile.video.smartcard.SmartCardControlActivity;
import com.star.mobile.video.util.CommonUtil;

public class ChangeSuccessActivity extends BaseActivity implements OnClickListener{
	
	private Button button_ok;
	private Button button_check;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pkg_success);
        initView();
	}
	private void initView() {
		button_ok=(Button)findViewById(R.id.btn_change_ok);
		button_check=(Button)findViewById(R.id.btn_change_check);
		button_ok.setOnClickListener(this);
		button_check.setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.verification);
	}
	@Override
	public void onClick(View v) {
		Intent intent =null;
		switch (v.getId()) {
		case R.id.btn_change_ok:
			intent = new Intent(this,SmartCardControlActivity.class);

			CommonUtil.startActivity(this, intent);
			finish();
			break;
		case R.id.btn_change_check:
			intent = new Intent(this,MyOrderActivity.class);
			CommonUtil.startActivity(this, intent);
			finish();
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	

}
