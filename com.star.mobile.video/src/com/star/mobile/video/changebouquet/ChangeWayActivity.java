package com.star.mobile.video.changebouquet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.cms.model.Package;
import com.star.cms.model.vo.SmartCardInfoVO;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.util.CommonUtil;

public class ChangeWayActivity extends BaseActivity implements OnClickListener{
	
	private Button button;
	private RelativeLayout rlDecoder;
	private RelativeLayout rlPhone;
	private int type;
	private ImageView ivDecoder;
	private ImageView ivPhone;
	private SmartCardInfoVO mSmartCardInfoVO;
	private Package changeToPkg;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pkg_select);
        Intent intent = getIntent();
		if (intent != null) {
			mSmartCardInfoVO = (SmartCardInfoVO) intent.getSerializableExtra("smartCardInfoVO");
			changeToPkg =(Package) intent.getSerializableExtra("changeToPkg");
		}
        initView();
	}
	private void initView() {
		ivDecoder = (ImageView)findViewById(R.id.iv_way_decoder);
		ivPhone = (ImageView)findViewById(R.id.iv_way_phone);
		rlDecoder = (RelativeLayout)findViewById(R.id.rl_decoder);
		rlPhone = (RelativeLayout)findViewById(R.id.rl_phone);
		button = (Button)findViewById(R.id.btn_change_way);
		rlDecoder.setOnClickListener(this);
		rlPhone.setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_actionbar_title)).setText(R.string.verification);
		setNoClickButton();
		ivDecoder.setImageResource(R.drawable.sel);
		ivPhone.setImageResource(R.drawable.no_sel);
		setButton();
		type = 1;
	}
	/**
	 * 设置点击按钮
	 */
	private void setButton() {
		button.setBackgroundResource(R.drawable.orange_button_bg);
		button.setOnClickListener(this);
	}
	/**
	 * 设置没有点击的button
	 */
	private void setNoClickButton(){
		button.setBackgroundResource(R.drawable.need_more_coins_button);
		button.setOnClickListener(null);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_decoder:
			ivDecoder.setImageResource(R.drawable.sel);
			ivPhone.setImageResource(R.drawable.no_sel);
			setButton();
			type = 1;
			break;
		case R.id.rl_phone:
			ivDecoder.setImageResource(R.drawable.no_sel);
			ivPhone.setImageResource(R.drawable.sel);
			setButton();
			type = 2;
			break;
		case R.id.btn_change_way:
			if(type ==1){
				Intent intent = new Intent(this,EnterDecoderNumberActivity.class);
				intent.putExtra("smartCardInfoVO", mSmartCardInfoVO);
				intent.putExtra("changeToPkg", changeToPkg);
				startActivityForResult(intent, 300);
			}else if(type ==2){
				Intent intent = new Intent(this,EnterPhoneNumberActivity.class);
				intent.putExtra("smartCardInfoVO", mSmartCardInfoVO);
				intent.putExtra("changeToPkg", changeToPkg);
				CommonUtil.startActivity(this, intent);
			}
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	

}
