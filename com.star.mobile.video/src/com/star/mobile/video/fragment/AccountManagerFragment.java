package com.star.mobile.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.star.cms.model.Area;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.CustomerServiceTimeAdapter;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.dialog.AlertDialog;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.me.mycoins.reward.RewardDetailActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.view.NoScrollListView;
import com.star.mobile.video.view.SmartCardListView;

public class AccountManagerFragment extends BaseFragment implements OnClickListener{
	
	private Activity homeActivity;
	private View mView;
	private NoScrollListView lvCustomerService;
	private SmartCardListView smardCardListView;
	private boolean goTopu;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mView != null){
			ViewGroup parent = (ViewGroup) mView.getParent();  
			if(parent != null) {  
				parent.removeView(mView);  
			}   
			return mView;
		}
		homeActivity = getActivity();
		mView = inflater.inflate(R.layout.fragment_account, null);
		mView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		initView();
		EggAppearService.appearEgg(getActivity(), EggAppearService.AccountManager);
		return mView;
	}

	private void initView() {
		lvCustomerService = (NoScrollListView) mView.findViewById(R.id.lv_customer_phone);
		List<Area> datas = new ArrayList<Area>();
		String phone = SharedPreferencesUtil.getCustomerPhone(homeActivity);
		String []phones;
		if(phone != null) {
			phones = phone.split("@");
			for(int i = 0;i<phones.length;i++) {
				Area area = new Area();
				area.setPhoneNumber(phones[i]);
				area.setNationalFlag(SharedPreferencesUtil.getNationalFlag(homeActivity));
				datas.add(area);
			}
		}
		CustomerServiceTimeAdapter adapter = new CustomerServiceTimeAdapter(homeActivity,datas,this);
		lvCustomerService.setAdapter(adapter);
		smardCardListView = (SmartCardListView) mView.findViewById(R.id.sc_list_view);
		if(!FunctionService.doHideFuncation(FunctionType.SmartCard)){
			smardCardListView.isShownAddCardView(true);
		}else{
			smardCardListView.setVisibility(View.GONE);
		}
		smardCardListView.setGoTopupActivity(goTopu);
	}
	
	public void setGoToupActivity(boolean isGo){
		this.goTopu = isGo;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!FunctionService.doHideFuncation(FunctionType.SmartCard))
			smardCardListView.getSmartCrad();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_call:
			//24小时服务热线
			callPhone((String)v.getTag());
			break;
		default:
			break;
		}
	}

	private void callPhone(String showPhoneNo) {
		final AlertDialog dialog = new AlertDialog(homeActivity, false);
		dialog.setMessage(showPhoneNo);
		dialog.setMessageTextColor(getResources().getColor(R.color.phomeno_text));
		dialog.setButtonText(getString(R.string.call));
		final String phoneNumber = showPhoneNo.replace("(", "").replace(")", "");
		dialog.setButtonOnClick(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phoneNumber));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
