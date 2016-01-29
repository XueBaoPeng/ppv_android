package com.star.mobile.video.account;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.FragmentActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.PhoneAndEmailView;
import com.star.mobile.video.view.PhoneAndEmailView.CallBack;

public class RegisterActivity extends FragmentActivity implements OnClickListener{

	public PhoneAndEmailView headView;
	public boolean isMobileRegister;
	public String verifCode; //验证码
	public String mobileNumber;//手机号
	public String selAreaNumber;//选中的区号
//	public boolean isSelMobileRegister = false;
	public String inviteCode;//邀请码
	private View contentView;
	private LinearLayout RegisterContentLinearLayout;
	private RelativeLayout phoneView;
	private RelativeLayout emailLine;
	private RelativeLayout centerLine;
	private  View title;
	private TextView request;
	
	private FragmentManager fragmentManager;
	private String currentTag;
	public final static String PHONE_CHECK = MobileRegisterCheckFragment.class.getSimpleName();
	public final static String PHONE_REGISTER = MobileRegisterContentFragment.class.getSimpleName();
	private final static String EMAIL_REGISTER = EmailRegisterContentFragment.class.getSimpleName();
	private static Map<String,Class<?>> fragments = new HashMap<String, Class<?>>();
	static{
		fragments.put(PHONE_CHECK, MobileRegisterCheckFragment.class);
		fragments.put(PHONE_REGISTER, MobileRegisterContentFragment.class);
		fragments.put(EMAIL_REGISTER, EmailRegisterContentFragment.class);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_register);
		fragmentManager = getSupportFragmentManager();
		initView();
		
	}

//	@Override
//	protected void onStart() {
//		super.onStart();
//		if(isSelMobileRegister) {
//			headView.selPhone();
//		} else {
//			headView.selEmail();
//		}
//	}
	private void initView() {
		title=findViewById(R.id.regist_title);
		request=(TextView) title.findViewById(R.id.tv_bouquet_btn);
		request.setVisibility(View.GONE);
		headView = (PhoneAndEmailView) findViewById(R.id.phone_email_view);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getResources().getString(R.string.reg_title));
		((TextView) findViewById(R.id.tv_bouquet_btn)).setText(getResources().getString(R.string.skip));
		((TextView) findViewById(R.id.tv_bouquet_btn)).setOnClickListener(this);
		((ImageView)findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		RegisterContentLinearLayout=(LinearLayout) findViewById(R.id.linearLayout);
 	 
		if(CommonUtil.getSelAreaNumber(RegisterActivity.this)==null){
			headView.GonePhoneView();
 			
		}
		headView.setEmailCallBack(new CallBack() {
			
			@Override
			public void onClick() {
				setFragment(EMAIL_REGISTER);
//				isSelMobileRegister = false;
			}
		});
		headView.setPhoneCallBack(new CallBack(){

			@Override
			public void onClick() {
				setFragment(PHONE_CHECK);
//				isSelMobileRegister = true;
			}
			
		});
		headView.initLayout(true);
	}
	
	
	public void setMobileRegisterContentFragment(String tag){
		if(tag.equals(currentTag)){
			return;
		}
		currentTag=tag;
		try {
			 
			FragmentTransaction transaction=fragmentManager.beginTransaction();
			Fragment fragment=(Fragment) fragments.get(tag).getConstructor().newInstance();
			RegisterContentLinearLayout.removeAllViews();
			transaction.replace(R.id.linearLayout,fragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		};
	}
	public void setFragment(String tag) {
		if(tag.equals(currentTag)) {
			return;
		}
		currentTag = tag;
		try {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			Fragment fragment = (Fragment) fragments.get(tag).getConstructor().newInstance();;
			transaction.replace(R.id.lv_fragment, fragment);
			transaction.addToBackStack(null);
			transaction.commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(!isMobileRegister) {
			CommonUtil.finishActivityAnimation(this);
			currentTag = null;
		} else {
			currentTag = PHONE_CHECK;
			isMobileRegister = false;
			super.onBackPressed();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_phone:
			setFragment(PHONE_CHECK);
			break;
		case R.id.iv_email:
			setFragment(EMAIL_REGISTER);
			break;
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;
		case R.id.tv_bouquet_btn:
			CommonUtil.startActivity(this, HomeActivity.class);
			finish();
			break;
		default:
			break;
		}
	}
}
