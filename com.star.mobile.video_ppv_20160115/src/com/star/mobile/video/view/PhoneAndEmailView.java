package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;

public class PhoneAndEmailView extends LinearLayout implements OnClickListener {
	
	private ImageView ivPhone;
	private ImageView ivEmail;
	private RelativeLayout phone_parent;
//	private TextView tvPhone;
//	private TextView tvEmail;
	private ImageView line_phone;
	private ImageView line_email;
	private CallBack phoneCallBack;
	private CallBack emailCallBack;
	private Context context;
	
	
	public PhoneAndEmailView(Context context) {
		this(context, null);
	}
	
	
	public PhoneAndEmailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_phone_email_head, this);
		this.context = context;
		ivPhone = (ImageView) findViewById(R.id.iv_phone);
		ivEmail = (ImageView) findViewById(R.id.iv_email);
//		tvPhone = (TextView) findViewById(R.id.tv_phone);
//		tvEmail = (TextView) findViewById(R.id.tv_email);
		phone_parent=(RelativeLayout) findViewById(R.id.phone_parent);
		line_phone=(ImageView) findViewById(R.id.line_phone);
		line_email=(ImageView) findViewById(R.id.line_email);
		ivEmail.setOnClickListener(this);
		ivPhone.setOnClickListener(this);
		initLayout(true);
	}


	public void initLayout(boolean isPhone) {
		if(FunctionService.doHideFuncation(FunctionType.RegisterWithPhone)){
//			findViewById(R.id.ll_phone).setVisibility(View.GONE);
//			findViewById(R.id.iv_ver_line).setVisibility(View.GONE);
			ivPhone.setBackgroundResource(R.drawable.ic_phone_blue);
			emailOnClick();
		}else if(isPhone){
			phoneOnClick();
		}else{
			emailOnClick();
		}
	}
	
	public void selPhone() {
		onClick(ivPhone);
		initLayout(true);
	}
	
	public void selEmail() {
		onClick(ivEmail);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_phone:
			phoneOnClick();
			break;
		case R.id.iv_email:
			emailOnClick();
			break;
		default:
			break;
		}
	}
	
	private void phoneOnClick() {
		ivPhone.setBackgroundResource(R.drawable.ic_phone_blue);
		ivEmail.setBackgroundResource(R.drawable.ic_email_green);
//		tvPhone.setTextColor(context.getResources().getColor(R.color.orange));
//		tvEmail.setTextColor(context.getResources().getColor(R.color.no_select_coupon_title));
		line_phone.setVisibility(View.VISIBLE);
		line_email.setVisibility(View.GONE);

		if(phoneCallBack != null) {
			phoneCallBack.onClick();
		}
	}
	
	private void emailOnClick() {
		 
//		tvPhone.setTextColor(context.getResources().getColor(R.color.no_select_coupon_title));
//		tvEmail.setTextColor(context.getResources().getColor(R.color.orange));
		ivPhone.setBackgroundResource(R.drawable.ic_phone_green);
		ivEmail.setBackgroundResource(R.drawable.ic_email_blue);
		line_phone.setVisibility(View.GONE);
		line_email.setVisibility(View.VISIBLE);
		if(emailCallBack != null) {
			emailCallBack.onClick();
		}
	}
	 
	
	public void GonePhoneView(){
		this.phone_parent.setVisibility(View.GONE);
	}
	
	 
	public void setPhoneCallBack(CallBack cb) {
		this.phoneCallBack = cb;
	}
	
	public void setEmailCallBack(CallBack cb) {
		this.emailCallBack = cb;
	}
	
	public interface CallBack{
		public void onClick();
	}
	

}
